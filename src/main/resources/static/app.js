const form = document.getElementById("alignmentForm");
const errorBox = document.getElementById("errorBox");
const loadingBox = document.getElementById("loadingBox");
const resultPanel = document.getElementById("resultPanel");
const playbackPanel = document.getElementById("playbackPanel");
const historyPanel = document.getElementById("historyPanel");
const alignmentBlock = document.getElementById("alignmentBlock");
const matrixContainer = document.getElementById("matrixContainer");
const jsonBlock = document.getElementById("jsonBlock");
const analyzeBtn = document.getElementById("analyzeBtn");
const resetBtn = document.getElementById("resetBtn");
const seq1Input = document.getElementById("sequence1");
const seq2Input = document.getElementById("sequence2");
const heatmapToggle = document.getElementById("heatmapToggle");
const pathToggle = document.getElementById("pathToggle");
const playbackRange = document.getElementById("playbackRange");
const playbackInfo = document.getElementById("playbackInfo");

let lastResult = null;
let currentToken = localStorage.getItem("dna-token") || "";
let playbackTimer = null;
let playbackIndex = 0;

const presets = {
    shortMatch: {sequence1: "ACGTACG", sequence2: "ACTACG", algorithmType: "GLOBAL"},
    mutation: {sequence1: "AATGCCGTTAAC", sequence2: "AATGTCGTCAAC", algorithmType: "GLOBAL"},
    evolution: {sequence1: "TTGACCGTACGAT", sequence2: "GGACCGTTCG", algorithmType: "LOCAL"}
};

initialize();

function initialize() {
    setupTheme();
    setupTabs();
    setupInputs();
    setupPresets();
    setupActions();
    refreshAuthState();
}

function setupTheme() {
    const themeToggle = document.getElementById("themeToggle");
    const savedTheme = localStorage.getItem("dna-theme");
    if (savedTheme === "dark") {
        document.body.classList.add("dark");
    }
    themeToggle.addEventListener("click", () => {
        document.body.classList.toggle("dark");
        localStorage.setItem("dna-theme", document.body.classList.contains("dark") ? "dark" : "light");
    });
}

function setupTabs() {
    const tabButtons = document.querySelectorAll(".tab-btn");
    const tabPanels = document.querySelectorAll(".tab-panel");
    tabButtons.forEach((button) => {
        button.addEventListener("click", () => {
            const tab = button.dataset.tab;
            tabButtons.forEach((b) => b.classList.remove("active"));
            button.classList.add("active");
            tabPanels.forEach((panel) => panel.classList.toggle("hidden", panel.dataset.panel !== tab));
        });
    });
}

function setupInputs() {
    [seq1Input, seq2Input].forEach((input, index) => {
        input.addEventListener("input", () => {
            input.value = sanitizeSequence(input.value);
            const metaId = index === 0 ? "seq1Meta" : "seq2Meta";
            const rulerId = index === 0 ? "ruler1" : "ruler2";
            updateSequenceMeta(metaId, input.value);
            updateRuler(rulerId, input.value.length);
        });
    });
    updateSequenceMeta("seq1Meta", "");
    updateSequenceMeta("seq2Meta", "");
    updateRuler("ruler1", 0);
    updateRuler("ruler2", 0);
}

function setupPresets() {
    document.querySelectorAll("[data-preset]").forEach((button) => {
        button.addEventListener("click", () => {
            const preset = presets[button.dataset.preset];
            seq1Input.value = preset.sequence1;
            seq2Input.value = preset.sequence2;
            document.getElementById("algorithmType").value = preset.algorithmType;
            updateSequenceMeta("seq1Meta", preset.sequence1);
            updateSequenceMeta("seq2Meta", preset.sequence2);
            updateRuler("ruler1", preset.sequence1.length);
            updateRuler("ruler2", preset.sequence2.length);
        });
    });
}

function setupActions() {
    form.addEventListener("submit", submitAlignment);
    resetBtn.addEventListener("click", resetForm);
    heatmapToggle.addEventListener("change", rerenderMatrix);
    pathToggle.addEventListener("change", rerenderMatrix);
    playbackRange.addEventListener("input", () => {
        playbackIndex = Number(playbackRange.value);
        renderPlayback();
    });
    document.getElementById("copyAlignmentBtn").addEventListener("click", copyAlignment);
    document.getElementById("downloadJsonBtn").addEventListener("click", downloadJson);
    document.getElementById("playBtn").addEventListener("click", startPlayback);
    document.getElementById("pauseBtn").addEventListener("click", stopPlayback);
    document.getElementById("stepBtn").addEventListener("click", stepPlayback);
    document.getElementById("resetPlaybackBtn").addEventListener("click", resetPlayback);

    document.getElementById("openLoginBtn").addEventListener("click", () => toggleAuthPanel(true));
    document.getElementById("closeAuthBtn").addEventListener("click", () => toggleAuthPanel(false));
    document.getElementById("logoutBtn").addEventListener("click", logout);
    document.getElementById("loginForm").addEventListener("submit", login);
    document.getElementById("registerForm").addEventListener("submit", register);
    document.getElementById("refreshHistoryBtn").addEventListener("click", loadHistory);
    document.getElementById("batchForm").addEventListener("submit", runBatch);
}

async function submitAlignment(event) {
    event.preventDefault();
    hideError();
    if (!currentToken) {
        showError("Please login first to run and save analyses.");
        return;
    }

    setLoading(true);
    const payload = {
        sequence1: sanitizeSequence(seq1Input.value.trim()),
        sequence2: sanitizeSequence(seq2Input.value.trim()),
        algorithmType: document.getElementById("algorithmType").value,
        matchScore: Number(document.getElementById("matchScore").value),
        mismatchScore: Number(document.getElementById("mismatchScore").value),
        gapPenalty: Number(document.getElementById("gapPenalty").value)
    };

    try {
        const data = await api("/api/alignment/analyze", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(payload)
        });
        lastResult = data;
        renderResult(data);
        resultPanel.classList.remove("hidden");
        playbackPanel.classList.remove("hidden");
        await loadHistory();
    } catch (error) {
        showError(error.message);
    } finally {
        setLoading(false);
    }
}

function renderResult(data) {
    document.getElementById("resultAlgorithm").textContent = data.algorithmType;
    document.getElementById("resultScore").textContent = data.score;
    document.getElementById("resultIdentity").textContent = `${(data.identityPercentage || 0).toFixed(2)}%`;
    document.getElementById("tracebackSteps").textContent = `${(data.tracebackPath || []).length}`;
    document.getElementById("resultExplanation").textContent = data.explanation;
    alignmentBlock.textContent = [data.alignedSequence1, data.alignmentMarker, data.alignedSequence2].join("\n");
    jsonBlock.textContent = JSON.stringify(data, null, 2);
    playbackRange.min = "1";
    playbackRange.max = String(Math.max(1, (data.tracebackPath || []).length));
    playbackRange.value = playbackRange.max;
    playbackIndex = Number(playbackRange.value);
    renderPlayback();
}

function renderPlayback() {
    if (!lastResult) {
        return;
    }
    const path = lastResult.tracebackPath || [];
    const total = path.length;
    const active = path.slice(0, Math.min(playbackIndex, total));
    playbackInfo.textContent = `Traceback step: ${active.length} / ${total}`;
    renderMatrix(lastResult, active);
}

function startPlayback() {
    if (!lastResult) {
        return;
    }
    stopPlayback();
    playbackTimer = setInterval(() => {
        const max = Number(playbackRange.max);
        if (playbackIndex >= max) {
            stopPlayback();
            return;
        }
        playbackIndex += 1;
        playbackRange.value = String(playbackIndex);
        renderPlayback();
    }, 450);
}

function stopPlayback() {
    if (playbackTimer) {
        clearInterval(playbackTimer);
        playbackTimer = null;
    }
}

function stepPlayback() {
    if (!lastResult) {
        return;
    }
    const max = Number(playbackRange.max);
    playbackIndex = Math.min(max, playbackIndex + 1);
    playbackRange.value = String(playbackIndex);
    renderPlayback();
}

function resetPlayback() {
    playbackIndex = 1;
    playbackRange.value = "1";
    renderPlayback();
}

function renderMatrix(data, activePath = data.tracebackPath || []) {
    const matrix = data.scoringMatrix || [];
    const seq1 = data.normalizedSequence1 || "";
    const seq2 = data.normalizedSequence2 || "";
    const pathSet = new Set(activePath.map((p) => `${p.row}-${p.col}`));
    const values = matrix.flat();
    const min = values.length ? Math.min(...values) : 0;
    const max = values.length ? Math.max(...values) : 0;
    const table = document.createElement("table");
    const header = document.createElement("tr");
    header.appendChild(createCell("th", "#"));
    header.appendChild(createCell("th", "-"));
    for (const ch of seq2) {
        header.appendChild(createCell("th", ch));
    }
    table.appendChild(header);

    for (let i = 0; i < matrix.length; i++) {
        const row = document.createElement("tr");
        row.appendChild(createCell("th", i === 0 ? "-" : seq1[i - 1]));
        for (let j = 0; j < matrix[i].length; j++) {
            const td = createCell("td", matrix[i][j]);
            if (heatmapToggle.checked) {
                td.style.backgroundColor = getHeatColor(matrix[i][j], min, max);
            }
            if (pathToggle.checked && pathSet.has(`${i}-${j}`)) {
                td.classList.add("path");
            }
            row.appendChild(td);
        }
        table.appendChild(row);
    }
    matrixContainer.innerHTML = "";
    matrixContainer.appendChild(table);
}

async function login(event) {
    event.preventDefault();
    const payload = {
        usernameOrEmail: document.getElementById("loginUser").value.trim(),
        password: document.getElementById("loginPass").value
    };
    try {
        const data = await api("/api/auth/login", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(payload)
        }, false);
        setToken(data.token);
        document.getElementById("authMessage").textContent = `Welcome back, ${data.username}.`;
        toggleAuthPanel(false);
        await refreshAuthState();
    } catch (error) {
        document.getElementById("authMessage").textContent = error.message;
    }
}

async function register(event) {
    event.preventDefault();
    const payload = {
        username: document.getElementById("regUser").value.trim(),
        email: document.getElementById("regEmail").value.trim(),
        password: document.getElementById("regPass").value
    };
    try {
        const data = await api("/api/auth/register", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(payload)
        }, false);
        setToken(data.token);
        document.getElementById("authMessage").textContent = `Account created for ${data.username}.`;
        toggleAuthPanel(false);
        await refreshAuthState();
    } catch (error) {
        document.getElementById("authMessage").textContent = error.message;
    }
}

async function logout() {
    try {
        if (currentToken) {
            await api("/api/auth/logout", {method: "POST"});
        }
    } catch (_) {
        // ignore logout failures when session already expired
    } finally {
        setToken("");
        await refreshAuthState();
    }
}

async function refreshAuthState() {
    const authStatus = document.getElementById("authStatus");
    const logoutBtn = document.getElementById("logoutBtn");
    if (!currentToken) {
        authStatus.textContent = "Not signed in";
        logoutBtn.classList.add("hidden");
        historyPanel.classList.add("hidden");
        setAppVisibility(false);
        toggleAuthPanel(true);
        return;
    }
    try {
        const profile = await api("/api/auth/me", {method: "GET"});
        authStatus.textContent = `Signed in as ${profile.username} (${profile.email})`;
        logoutBtn.classList.remove("hidden");
        historyPanel.classList.remove("hidden");
        setAppVisibility(true);
        toggleAuthPanel(false);
        await loadHistory();
    } catch (_) {
        setToken("");
        authStatus.textContent = "Session expired. Please login again.";
        logoutBtn.classList.add("hidden");
        historyPanel.classList.add("hidden");
        setAppVisibility(false);
        toggleAuthPanel(true);
    }
}

async function loadHistory() {
    if (!currentToken) {
        return;
    }
    const list = document.getElementById("historyList");
    const items = await api("/api/alignment/history", {method: "GET"});
    if (!items.length) {
        list.innerHTML = "<p class='hint'>No analysis history yet.</p>";
        return;
    }
    list.innerHTML = items.map((item) => `
        <article class="history-item">
            <h4>${item.algorithmType} | Score ${item.score} | Identity ${(item.identityPercentage || 0).toFixed(2)}%</h4>
            <p>${item.sequence1} vs ${item.sequence2}</p>
            <p>${new Date(item.createdAt).toLocaleString()}</p>
        </article>
    `).join("");
}

async function runBatch(event) {
    event.preventDefault();
    if (!currentToken) {
        showError("Login required for batch reports.");
        return;
    }
    const file = document.getElementById("fastaFile").files[0];
    if (!file) {
        showError("Please choose a FASTA file.");
        return;
    }
    const formData = new FormData();
    formData.append("file", file);
    formData.append("algorithmType", document.getElementById("batchAlgorithmType").value);
    formData.append("matchScore", document.getElementById("matchScore").value);
    formData.append("mismatchScore", document.getElementById("mismatchScore").value);
    formData.append("gapPenalty", document.getElementById("gapPenalty").value);

    const status = document.getElementById("batchStatus");
    status.textContent = "Running batch analysis...";
    try {
        const report = await api("/api/alignment/batch-fasta", {method: "POST", body: formData});
        status.textContent = `Reference: ${report.referenceHeader} | Compared sequences: ${report.results.length}`;
        renderBatchReport(report);
    } catch (error) {
        status.textContent = error.message;
    }
}

function renderBatchReport(report) {
    const wrap = document.getElementById("batchReportWrap");
    const rows = report.results.map((item) => `
        <tr>
            <td>${escapeHtml(item.header)}</td>
            <td>${item.score}</td>
            <td>${(item.identityPercentage || 0).toFixed(2)}%</td>
            <td><pre class="json">${escapeHtml([item.alignedSequence1, item.alignmentMarker, item.alignedSequence2].join("\n"))}</pre></td>
        </tr>
    `).join("");
    wrap.innerHTML = `
        <table>
            <tr><th>Target</th><th>Score</th><th>Identity</th><th>Alignment</th></tr>
            ${rows}
        </table>
    `;
    wrap.classList.remove("hidden");
}

async function api(url, options = {}, useAuth = true) {
    const headers = {...(options.headers || {})};
    if (useAuth && currentToken) {
        headers.Authorization = `Bearer ${currentToken}`;
    }
    const response = await fetch(url, {...options, headers});
    let data = {};
    try {
        data = await response.json();
    } catch (_) {
        data = {};
    }
    if (!response.ok) {
        throw new Error(data.message || "Request failed.");
    }
    return data;
}

function toggleAuthPanel(show) {
    document.getElementById("authPanel").classList.toggle("hidden", !show);
}

function setAppVisibility(isLoggedIn) {
    document.querySelectorAll(".requires-auth").forEach((element) => {
        element.classList.toggle("hidden", !isLoggedIn);
    });
}

function setToken(token) {
    currentToken = token || "";
    if (currentToken) {
        localStorage.setItem("dna-token", currentToken);
    } else {
        localStorage.removeItem("dna-token");
    }
}

function createCell(tag, value) {
    const element = document.createElement(tag);
    element.textContent = value;
    return element;
}

function getHeatColor(value, min, max) {
    if (max === min) return "rgba(31, 111, 235, 0.10)";
    const ratio = (value - min) / (max - min);
    const alpha = 0.08 + ratio * 0.35;
    return `rgba(31, 111, 235, ${alpha.toFixed(3)})`;
}

function sanitizeSequence(value) {
    return value.toUpperCase().replace(/[^ACGT]/g, "");
}

function updateRuler(targetId, length) {
    let ruler = "";
    for (let i = 1; i <= Math.max(length, 1); i++) {
        ruler += i % 10 === 0 ? String((i / 10) % 10) : ".";
    }
    document.getElementById(targetId).textContent = ruler;
}

function updateSequenceMeta(targetId, sequence) {
    const length = sequence.length;
    const gcCount = (sequence.match(/[GC]/g) || []).length;
    const gcPercent = length === 0 ? 0 : (gcCount * 100) / length;
    document.getElementById(targetId).textContent = `Length: ${length} | GC: ${gcPercent.toFixed(1)}%`;
}

function rerenderMatrix() {
    if (lastResult) renderPlayback();
}

function copyAlignment() {
    if (!lastResult) return showError("Run analysis first.");
    navigator.clipboard.writeText(alignmentBlock.textContent)
        .then(() => {
            loadingBox.textContent = "Alignment copied to clipboard.";
            loadingBox.classList.remove("hidden");
            setTimeout(() => {
                loadingBox.textContent = "Computing alignment matrix...";
                loadingBox.classList.add("hidden");
            }, 1000);
        })
        .catch(() => showError("Clipboard access denied."));
}

function downloadJson() {
    if (!lastResult) return showError("Run analysis first.");
    const blob = new Blob([JSON.stringify(lastResult, null, 2)], {type: "application/json"});
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "alignment-result.json";
    a.click();
    URL.revokeObjectURL(url);
}

function resetForm() {
    form.reset();
    seq1Input.value = "";
    seq2Input.value = "";
    updateSequenceMeta("seq1Meta", "");
    updateSequenceMeta("seq2Meta", "");
    updateRuler("ruler1", 0);
    updateRuler("ruler2", 0);
    hideError();
    stopPlayback();
    setLoading(false);
    lastResult = null;
    resultPanel.classList.add("hidden");
    playbackPanel.classList.add("hidden");
}

function setLoading(isLoading) {
    analyzeBtn.disabled = isLoading;
    loadingBox.classList.toggle("hidden", !isLoading);
}

function showError(message) {
    errorBox.textContent = message;
    errorBox.classList.remove("hidden");
}

function hideError() {
    errorBox.textContent = "";
    errorBox.classList.add("hidden");
}

function escapeHtml(text) {
    return text
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;");
}

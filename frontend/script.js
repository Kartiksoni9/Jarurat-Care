// ===== CONFIG =====
const API_BASE = "http://localhost:8080/api";

// ===== FORM TYPE TOGGLE =====
function setType(type, btn) {
  document.getElementById("type").value = type;
  document.querySelectorAll(".toggle-btn").forEach(b => b.classList.remove("active"));
  btn.classList.add("active");
}

// ===== FORM SUBMISSION =====
document.getElementById("registerForm").addEventListener("submit", async function (e) {
  e.preventDefault();

  const btn = document.getElementById("submitBtn");
  const msgEl = document.getElementById("formMsg");

  const payload = {
    name: document.getElementById("name").value.trim(),
    email: document.getElementById("email").value.trim(),
    phone: document.getElementById("phone").value.trim(),
    type: document.getElementById("type").value,
    message: document.getElementById("message").value.trim(),
  };

  btn.disabled = true;
  btn.textContent = "Submitting...";
  msgEl.className = "form-msg";

  try {
    const res = await fetch(`${API_BASE}/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });

    const data = await res.json();

    if (res.ok && data.success) {
      msgEl.className = "form-msg success";
      msgEl.textContent = data.message;
      document.getElementById("registerForm").reset();
      document.getElementById("type").value = "patient";
    } else {
      throw new Error(data.message || "Something went wrong");
    }
  } catch (err) {
    msgEl.className = "form-msg error";
    msgEl.textContent = "⚠️ " + (err.message || "Could not submit. Please try again.");
  } finally {
    btn.disabled = false;
    btn.textContent = "Submit Registration";
  }
});

// ===== CHATBOT =====
// No API key here — key lives safely in the backend (application.properties)
const MAX_HISTORY = 10; // keep last 10 messages to avoid token bloat
let chatHistory = [];

async function sendMessage() {
  const input = document.getElementById("chatInput");
  const msg = input.value.trim();
  if (!msg) return;

  input.value = "";
  appendMessage(msg, "user");
  chatHistory.push({ role: "user", parts: [{ text: msg }] });

  // Trim to last MAX_HISTORY messages before sending
  const trimmedHistory = chatHistory.slice(-MAX_HISTORY);

  const typingEl = appendMessage("Typing...", "typing");

  try {
    // Call your own backend — NOT Gemini directly
    const res = await fetch(`${API_BASE}/chat`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ history: trimmedHistory }),
    });

    const data = await res.json();

    if (!res.ok) throw new Error(data.error || "Something went wrong");

    const reply = data.reply || "I'm sorry, I couldn't process that. Please try again.";

    typingEl.remove();
    appendMessage(reply, "bot");
    chatHistory.push({ role: "model", parts: [{ text: reply }] });

    // Keep in-memory history trimmed too
    if (chatHistory.length > MAX_HISTORY) {
      chatHistory = chatHistory.slice(-MAX_HISTORY);
    }

  } catch (err) {
    typingEl.remove();
    appendMessage("Sorry, I'm having trouble connecting right now. Please try again shortly.", "bot");
  }
}

function appendMessage(text, role) {
  const container = document.getElementById("chatMessages");
  const bubble = document.createElement("div");
  bubble.className = `chat-bubble ${role}`;
  bubble.textContent = text;
  container.appendChild(bubble);
  container.scrollTop = container.scrollHeight;
  return bubble;
}

function askSuggestion(btn) {
  document.getElementById("chatInput").value = btn.textContent;
  sendMessage();
}

// ===== SMOOTH SCROLL =====
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
  anchor.addEventListener("click", function (e) {
    e.preventDefault();
    const target = document.querySelector(this.getAttribute("href"));
    if (target) target.scrollIntoView({ behavior: "smooth", block: "start" });
  });
});
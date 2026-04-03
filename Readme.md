# 🎗️ Jarurat Care – Mini Healthcare Support Web App

A web application built for **Jarurat Care**, India's largest cancer care community NGO. The app allows patients, caregivers, and volunteers to register for support, and includes an **AI-powered FAQ chatbot** for instant assistance.

---

## 🔗 Links
- **Live Site:** `[YOUR_NETLIFY_LINK]`
- **Backend API:** `[YOUR_RENDER_LINK]`
- **GitHub:** `[YOUR_GITHUB_LINK]`

---

## 🛠️ Tech Stack

| Layer | Technology                      |
|---|---------------------------------|
| Frontend | HTML5, CSS3, Vanilla JavaScript |
| Backend | Java 17 + Spring Boot 3.2       |
| AI Feature | Gemini Free Api                 |
| Frontend Hosting | Vercel                          |
| Backend Hosting | Railway (Free Tier)             |
| Build Tool | Maven                           |

---

## 🤖 AI Feature – FAQ Chatbot

The app includes a **Gemini AI-powered chatbot** that:
- Answers questions about Jarurat Care's programs and services
- Guides users through the registration process
- Provides caregiver support information
- Handles FAQs about cancer awareness and volunteer opportunities
- Responds with compassion and emotional sensitivity

**Implementation:** Direct API call to Gemini's `/v1/messages` endpoint from the frontend, with a carefully crafted system prompt that keeps the bot focused on Jarurat Care's mission and context.

---

## 🌐 NGO Use-Case

**Problem:** Cancer patients and caregivers often don't know where to start — what support is available, how to volunteer, what to expect.

**Solution:** This app provides:
1. A **simple registration form** for patients needing support and individuals wanting to volunteer
2. An **AI chatbot** that provides instant, compassionate answers 24/7 — reducing the burden on the NGO's human support team
3. A clean, accessible interface that works on mobile and desktop

**Impact:** The chatbot can handle hundreds of simultaneous FAQs, freeing up NGO staff to focus on high-touch, personal support for the families who need it most.

---

## 🚀 Running Locally

### Backend (Spring Boot)
```bash
cd backend
./mvnw spring-boot:run
```
API runs at `http://localhost:8080`

### Frontend
Open `frontend/index.html` in your browser directly, or use Live Server in VS Code.

---

## 📡 API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/health` | Health check |
| POST | `/api/register` | Submit registration form |
| GET | `/api/registrations` | View all registrations |

### POST `/api/register` – Request Body
```json
{
  "name": "Rahul Sharma",
  "email": "rahul@email.com",
  "phone": "+91 98765 43210",
  "type": "patient",
  "message": "Looking for caregiver support"
}
```

---

## ⚙️ Configuration

In `frontend/script.js`, update:
```js
const API_BASE = "YOUR_BACKEND_URL/api";
const CLAUDE_API_KEY = "YOUR_API_KEY";
```

> **Note:** For production, the Gemini API call should be proxied through the Spring Boot backend to keep the API key secure.

---

## 👨‍💻 Author
Built as part of an internship assignment for Jarurat Care NGO.
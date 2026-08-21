const { onRequest } = require("firebase-functions/v2/https");
const { defineSecret } = require("firebase-functions/params");
const { GoogleGenerativeAI } = require("@google/generative-ai");

const geminiApiKey = defineSecret("GEMINI_API_KEY");

exports.geminiProxy = onRequest(
  {
    region: "europe-west1",
    cors: true,
    timeoutSeconds: 60,
    memory: "256MiB",
    secrets: [geminiApiKey],
  },
  async (req, res) => {
    if (req.method !== "POST") {
      res.status(405).json({ error: "POST only" });
      return;
    }

    const body = req.body;
    if (!body || !Array.isArray(body.contents)) {
      res.status(400).json({ error: "Invalid Gemini request" });
      return;
    }

    try {
      const client = new GoogleGenerativeAI(geminiApiKey.value());
      const model = client.getGenerativeModel({
        model: "gemini-2.5-flash",
        systemInstruction: body.systemInstruction,
      });

      const contents = body.contents.map((content) => ({
        role: content.role || "user",
        parts: (content.parts || []).map((part) => {
          if (part.inlineData) {
            return { inlineData: part.inlineData };
          }
          return { text: part.text || "" };
        }),
      }));

      const result = await model.generateContent({
        contents,
        generationConfig: body.generationConfig,
      });
      res.status(200).json(result.response);
    } catch (error) {
      console.error("Gemini proxy failure", error);
      res.status(502).json({ error: "Gemini service unavailable" });
    }
  }
);

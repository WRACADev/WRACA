const express = require("express");
const mongoose = require("mongoose");
const bodyParser = require("body-parser");
const cors = require("cors");

const app = express();

// Middleware
app.use(bodyParser.json());
app.use(cors());

// Connect to MongoDB
mongoose.connect("mongodb://localhost:27017/chat", {
  useNewUrlParser: true,
  useUnifiedTopology: true,
});

// Message schema
const MessageSchema = new mongoose.Schema({
  forumId: Number,
  message: String,
  timestamp: { type: Date, default: Date.now },
});

const Message = mongoose.model("Message", MessageSchema);

// API routes
app.get("/messages/:forumId", async (req, res) => {
  const messages = await Message.find({ forumId: req.params.forumId });
  res.json(messages);
});

app.post("/messages", async (req, res) => {
  const newMessage = new Message(req.body);
  await newMessage.save();
  res.json(newMessage);
});

const PORT = process.env.PORT || 5000;
app.listen(PORT, () => console.log(`Server started on port ${PORT}`));

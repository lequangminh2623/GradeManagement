import React, { useEffect, useState, useContext, useRef } from "react";
import { db } from "../configs/Firebase";
import {
  collection,
  addDoc,
  onSnapshot,
  serverTimestamp,
  doc,
  setDoc
} from "firebase/firestore";
import { MyUserContext } from "../configs/MyContexts";
import ReactMarkdown from "react-markdown"; // Import ReactMarkdown
import "../styles/ChatPanel.css";

export default function ChatPanel({ selectedUser, onChatbotInteraction }) {
  const user = useContext(MyUserContext);
  const [text, setText] = useState("");
  const [messages, setMessages] = useState([]);
  const [loading, setLoading] = useState(false);
  const endRef = useRef();
  const chatHistoryRef = useRef();
  const current = user.email;
  const other = selectedUser.email;
  const chatId = [current, other].sort().join("_");
  const chatsRef = doc(db, "chats", chatId);
  const messagesColl = collection(db, "chats", chatId, "messages");

  useEffect(() => {
    if (!selectedUser) return;

    const chatId = [current, selectedUser.email].sort().join("_");
    const messagesColl = collection(db, "chats", chatId, "messages");

    const unsubscribe = onSnapshot(messagesColl, snapshot => {
      const msgs = snapshot.docs.map(doc => ({
        id: doc.id,
        ...doc.data(),
      }));
      msgs.sort((a, b) => a.timestamp?.toMillis() - b.timestamp?.toMillis());
      setMessages(msgs);
    });

    return () => unsubscribe();
  }, [selectedUser, current]);

  useEffect(() => {
    const chatHistory = chatHistoryRef.current;
    if (!chatHistory) return;

    // Scroll to the bottom only if the user is already at the bottom
    const isAtBottom =
      chatHistory.scrollHeight - chatHistory.scrollTop === chatHistory.clientHeight;

    endRef.current?.scrollIntoView({ behavior: "auto" });
  }, [messages, loading]);

  const sendMessage = async e => {
    e.preventDefault();
    if (!text.trim()) return;

    // Step 1: Save the user's message to Firestore
    const userMessage = {
      text,
      sender: current,
      timestamp: serverTimestamp()
    };

    const userMessageRef = await addDoc(messagesColl, userMessage);

    await setDoc(
      chatsRef,
      {
        participants: [current, other],
        lastMessage: text,
        lastSender: current,
        updatedAt: serverTimestamp()
      },
      { merge: true }
    );

    setText(""); // Clear the input field
    endRef.current?.scrollIntoView({ behavior: "smooth" });

    // Step 2: Send the user's message to the chatbot API (if applicable)
    if (onChatbotInteraction) {
      setLoading(true); // Show loading indicator
      try {
        const chatbotResponse = await onChatbotInteraction(text);

        // Step 3: Save the chatbot's response to Firestore
        const chatbotMessage = {
          text: chatbotResponse,
          sender: "chatbot@ou.edu.vn",
          timestamp: serverTimestamp()
        };

        await addDoc(messagesColl, chatbotMessage);

        await setDoc(
          chatsRef,
          {
            participants: [current, other],
            lastMessage: chatbotResponse,
            lastSender: "chatbot@ou.edu.vn",
            updatedAt: serverTimestamp()
          },
          { merge: true }
        );
      } catch (err) {
        console.error("Error during chatbot interaction:", err);
      } finally {
        setLoading(false); // Hide loading indicator
      }
    }
  };

  return (
    <div className="chat-panel">
      <div className="chat-header">
        <h5>{`${selectedUser.lastName} ${selectedUser.firstName}`}</h5>
      </div>
      <div className="chat-history" ref={chatHistoryRef}>
        {messages.map(m => (
          <div
            key={m.id}
            className={`msg ${m.sender === current ? "own" : "other"}`}
          >
            <div className="msg-text">
              <ReactMarkdown>{m.text}</ReactMarkdown> {/* Render Markdown */}
            </div>
          </div>
        ))}
        {loading && (
          <div className="msg chatbot">
            <div className="msg-text">Đang trả lời...</div>
          </div>
        )}
        <div ref={endRef} />
      </div>
      <form className="chat-input" onSubmit={sendMessage}>
        <input
          value={text}
          onChange={e => setText(e.target.value)}
          placeholder="Nhập tin nhắn..."
          disabled={loading} // Disable input while loading
        />
        <button type="submit" disabled={loading}>Gửi</button>
      </form>
    </div>
  );
}
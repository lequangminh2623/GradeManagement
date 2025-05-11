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
import "../styles/ChatPanel.css";

export default function ChatPanel({ selectedUser }) {
  const user = useContext(MyUserContext);
  const [text, setText] = useState("");
  const [messages, setMessages] = useState([]);
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
    endRef.current?.scrollIntoView({ behavior: "auto" });
  }, [messages]);

  const sendMessage = async e => {
    e.preventDefault();
    if (!text.trim()) return;

    await addDoc(messagesColl, {
      text,
      sender: current,
      timestamp: serverTimestamp()
    });

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

    setText("");
    endRef.current?.scrollIntoView({ behavior: "auto" });
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
            <div className="msg-text">{m.text}</div>
          </div>
        ))}
        <div ref={endRef} />
      </div>
      <form className="chat-input" onSubmit={sendMessage}>
        <input
          value={text}
          onChange={e => setText(e.target.value)}
          placeholder="Nhập tin nhắn..."
        />
        <button type="submit">Gửi</button>
      </form>
    </div>
  );
}
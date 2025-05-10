import React, { useEffect, useState, useContext, useRef } from "react";
import { db } from "../configs/Firebase";
import {
  collection,
  addDoc,
  onSnapshot,
  query,
  orderBy,
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

  const current = user.email;
  const other = selectedUser.email;
  const chatId = [current, other].sort().join("_");
  const chatsRef = doc(db, "chats", chatId);
  const messagesColl = collection(db, "chats", chatId, "messages");

  useEffect(() => {
    const q = query(messagesColl, orderBy("timestamp"));
    const unsub = onSnapshot(q, snap => {
      setMessages(snap.docs.map(d => ({ id: d.id, ...d.data() })));
    });
    return () => unsub();
  }, [selectedUser]);

  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: "smooth" });
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
  };

  return (
    <div className="chat-panel">
      <div className="chat-header">
        <h5>{`${selectedUser.lastName} ${selectedUser.firstName}`}</h5>
      </div>
      <div className="chat-history">
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
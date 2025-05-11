import React from "react";
import "../styles/Sidebar.css";
import TimeConvert from "./layouts/TimeConvert";

export default function Sidebar({
  users,
  currentEmail,
  selectedUser,
  onSelect,
  previews = {},
  onEndReach
}) {

  const loadingRef = React.useRef(false);

  const handleScroll = (e) => {
    const { scrollTop, scrollHeight, clientHeight } = e.currentTarget;
    if (
      !loadingRef.current &&
      scrollHeight > clientHeight &&
      scrollTop + clientHeight >= scrollHeight - 10
    ) {
      loadingRef.current = true;
      Promise.resolve(onEndReach())
        .finally(() => {
          loadingRef.current = false;
        });
    }
  };

  return (
    <div
      className="sidebar"
      onScroll={handleScroll}
    >
      <h3>Chats</h3>
      <ul className="user-list">
        {Array.isArray(users) &&
          users
            .map(u => {
              const fullName = `${u.lastName} ${u.firstName}`;
              const p = previews[u.email] || {};
              const previewText = p.text
                ? (p.sender === currentEmail ? `You: ${p.text}` : p.text)
                : "No messages yet";

              return (
                <li
                  key={u.email}
                  className={selectedUser?.email === u.email ? "active" : ""}
                  onClick={() => onSelect(u)}
                >
                  <div className="user-item">
                    <img
                      className="avatar"
                      src={u.avatar}
                      alt={fullName}
                    />
                    <div className="user-meta">
                      <div className="user-name">{fullName}</div>
                      <div className="user-preview">{previewText}</div>
                    </div>
                    <div className="user-time">
                      {p.timestamp && <TimeConvert timestamp={p.timestamp} />}
                    </div>
                  </div>
                </li>
              );
            })}
      </ul>
    </div>
  );
}
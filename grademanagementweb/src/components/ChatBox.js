import React, { useEffect, useState, useContext, useCallback } from "react";
import Apis, { endpoints } from "../configs/Apis";
import Sidebar from "./Sidebar";
import ChatPanel from "./ChatPanel";
import { MyUserContext } from "../configs/MyContexts";
import { collection, onSnapshot, orderBy, query, where } from "firebase/firestore";
import { db } from "../configs/Firebase";
import { useSearchParams } from "react-router-dom";

export default function ChatBox() {
  const user = useContext(MyUserContext);
  const [users, setUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);
  const [previews, setPreviews] = useState({});
  const [page, setPage] = useState(1);
  const [loading, setLoading] = useState(false);
  const [q] = useSearchParams();

  const fetchUsers = useCallback(async () => {
    try {
      setLoading(true);
      let url = `${endpoints['users']}?page=${page}`;
      const kw = q.get('kw');
      if (kw) {
        url += `&kw=${kw}`;
      }
      const res = await Apis.get(url);
      const data = res.data;
      if (data.length === 0) {
        setPage(0);
      } else {
        if (page === 1) {
          setUsers(data.filter(u => u.role !== "ROLE_ADMIN"));
        } else {
          setUsers(prev => [...prev, ...data.filter(u => u.role !== "ROLE_ADMIN")]);
        }
      }
    } catch (err) {
      console.error("Error fetching users:", err);
    } finally {
      setLoading(false);
    }
  }, [page, q]);

  useEffect(() => {
    if (page > 0) {
      fetchUsers();
    }
  }, [page, fetchUsers]);

  useEffect(() => {
    setPage(1);
    setUsers([]);
  }, [q]);

  const loadMore = () => {
    if (!loading && page > 0) {
      setPage(prev => prev + 1);
    }
  };

  useEffect(() => {
    if (!user?.email) return;

    const userEmail = user.email;
    const q = query(
      collection(db, "chats"),
      where("participants", "array-contains", userEmail),
      orderBy("updatedAt", "desc")
    );

    const unsub = onSnapshot(q, snapshot => {
      const map = {};
      snapshot.forEach(doc => {
        const data = doc.data();
        const other = data.participants.find(e => e !== userEmail);
        map[other] = {
          text: data.lastMessage,
          timestamp: data.updatedAt?.toMillis() || 0,
          sender: data.lastSender
        };
      });
      setPreviews(map);
    });

    return () => unsub();
  }, [user]);

  if (!user) {
    return <div>Đang tải người dùng...</div>;
  }

  return (
    <div style={{ display: "flex", height: "85vh" }}>
      <Sidebar
        users={users}
        currentEmail={user.email}
        selectedUser={selectedUser}
        onSelect={setSelectedUser}
        previews={previews}
        onEndReach={loadMore}
      />

      {selectedUser ? (
        <ChatPanel selectedUser={selectedUser} />
      ) : (
        <div style={{
          flex: 1,
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          color: "#666"
        }}>
          Chọn một người để bắt đầu chat
        </div>
      )}
    </div>
  );
}

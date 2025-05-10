import { initializeApp } from "firebase/app";
import { getFirestore } from "firebase/firestore";

const firebaseConfig = {
  apiKey: "AIzaSyD5GBjr5sVELjf3qb9cyPso5CT0Xr4nqhs",
  authDomain: "grademanagementweb.firebaseapp.com",
  projectId: "grademanagementweb",
  storageBucket: "grademanagementweb.firebasestorage.app",
  messagingSenderId: "91060012434",
  appId: "1:91060012434:web:9309b962afb238b3eead4b",
  measurementId: "G-9PC8VBSZN2"
};

const app = initializeApp(firebaseConfig);
export const db = getFirestore(app);

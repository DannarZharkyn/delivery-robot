import { initializeApp } from "firebase/app";
import { getAuth } from "firebase/auth";
import { getStorage } from "firebase/storage";
import { getFirestore } from "firebase/firestore";

const firebaseConfig = {
  apiKey: "YOUR_VALUE",
  authDomain: "YOUR_VALUE",
  databaseURL: "YOUR_VALUE",
  projectId: "YOUR_VALUE",
  storageBucket: "YOUR_VALUE",
  messagingSenderId: "YOUR_VALUE",
  appId: "YOUR_VALUE",
};


// Initialize Firebase
export const app = initializeApp(firebaseConfig);
export const auth = getAuth();
export const storage = getStorage();
export const db = getFirestore()

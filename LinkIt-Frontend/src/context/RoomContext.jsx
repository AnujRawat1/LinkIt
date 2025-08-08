import { createContext, useContext, useState, useEffect } from 'react';

const RoomContext = createContext();

export const RoomProvider = ({ children }) => {
  const [roomId, setRoomId] = useState(null);
  const [name, setName] = useState(null);
  const [code, setCode] = useState('');
  const [connected, setConnected] = useState(false);

  // ✅ Load from localStorage on page load
  useEffect(() => {
    const storedRoomId = localStorage.getItem('roomId');
    const storedName = localStorage.getItem('name');

    if (storedRoomId) setRoomId(storedRoomId);
    if (storedName) setName(storedName);
  }, []);

  // ✅ Save to localStorage whenever they change
  useEffect(() => {
    if (roomId) localStorage.setItem('roomId', roomId);
    if (name) localStorage.setItem('name', name);
  }, [roomId, name]);

  return (
    <RoomContext.Provider value={{
      roomId, setRoomId,
      name, setName,
      code, setCode,
      connected, setConnected
    }}>
      {children}
    </RoomContext.Provider>
  );
};

export default function useRoomContext() {
  return useContext(RoomContext);
}

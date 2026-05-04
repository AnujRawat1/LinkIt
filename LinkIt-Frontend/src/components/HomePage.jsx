import React, { useState } from 'react';
import { toast } from 'react-toastify';
import { createRoom, joinRoom } from '../services/RoomService';
import useRoomContext from '../context/RoomContext';
import { useNavigate } from 'react-router-dom';

const HomePage = () => {
  const [activeTab, setActiveTab] = useState('create');
  // const [name, setName] = useState('');
  // const [roomId, setRoomId] = useState('');

  const {roomId, setRoomId, name, setName} = useRoomContext();
  const navigate = useNavigate();

  const handleCreate = async (e) => {
    e.preventDefault();

    try {
      console.log("Creating Room:", { name });

      // Call the API
      const response = await createRoom(name);

      if (response != null && response.roomId) {
        // Join the room page now
        setRoomId(response.roomId); // ✅ Set roomId in context
        setName(name);              // ✅ Set name in context
        navigate(`/room/${response.roomId}`);
        console.log("Created Room:", { name });
      } else {
        throw new Error("No room ID received");
      }

    } catch (error) {
      console.error(error);
      toast.error("❌ Error creating room");
    }
  };


  const handleJoin = async (e) => {
    e.preventDefault();
    
    if (roomId.trim() && name.trim()) {
      console.log("Joining Room:", { name, roomId });
      const response = await joinRoom(roomId, name);
      console.log(response);
      
      if ( response ) { 
        toast.success("Joining Room");
        navigate(`/room/${roomId.trim()}`);
        setName("");
      }else {
        setRoomId("");
        toast.error("Room Does not exist."); 
        console.error("Room Does not exist."); 
      }

    } else {
      alert("❌ Please enter a valid Room ID.");
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-[#2e005f] to-[#110027] flex items-center justify-center px-6 py-10 text-white">
      <div className="w-full max-w-6xl flex flex-col md:flex-row items-center justify-between gap-12">

        {/* Left Side: Intro Content */}
        <div className="flex-1 text-center md:text-left">
          <h1 className="text-3xl sm:text-4xl font-bold mb-4">
            Sync your Ideas across devices  Instantly & Effortlessly.
          </h1>
          <p className="text-base sm:text-lg text-violet-200 mb-6 italic">
            Because Ctrl + V deserves more power.
          </p>
          <h2 className="text-3xl sm:text-5xl font-extrabold text-violet-400 ">
            {/* 𝐑𝐞𝐚𝐥𝐓𝐢𝐦𝐞 𝐂𝐨𝐝𝐞 𝐄𝐝𝐢𝐭𝐨𝐫 */}
            𝕽𝖊𝖆𝖑𝕿𝖎𝖒𝖊 𝕮𝖔𝖉𝖊 𝕰𝖉𝖎𝖙𝖔𝖗
          </h2>
        </div>

        {/* Right Side: Form Card */}
        <div className="flex-1 w-full max-w-md p-8 bg-white/10 backdrop-blur-md rounded-xl shadow-lg">
          <div className="flex flex-col items-center mb-6">
            <div className="flex flex-row items-center gap-3 mb-2">
                <div className="bg-violet-500 p-2 rounded-full">
                <svg
                    xmlns="http://www.w3.org/2000/svg"
                    className="h-8 w-8 text-white"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                >
                    <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M16 18l6-6-6-6M8 6l-6 6 6 6"
                    />
                </svg>
                </div>
                <h1 className="text-3xl font-bold text-white">LinkIt</h1>
            </div>
            <p className="text-sm text-violet-200">Real-time Code Sharing</p>
            </div>

          {/* Tabs */}
          <div className="mb-6">
            <div className="flex gap-2 mb-4">
              <button
                onClick={() => setActiveTab('join')}
                data-testid="join-tab-btn"
                className={`w-full py-2 rounded-md font-medium ${activeTab === 'join' ? 'bg-white/20 text-white' : 'bg-white/5 text-violet-300 hover:bg-white/10'}`}
              >
                ⏎ Join Clipboard
              </button>
              <button
                onClick={() => setActiveTab('create')}
                data-testid="create-tab-btn"
                className={`w-full py-2 rounded-md font-medium ${activeTab === 'create' ? 'bg-white/20 text-white' : 'bg-white/5 text-violet-300 hover:bg-white/10'}`}
              >
                ＋ Create Clipboard
              </button>
            </div>

            {/* Form */}
            <form onSubmit={activeTab === 'create' ? handleCreate : handleJoin} className="space-y-4">
              <div>
                <label className="block text-sm text-violet-200 mb-1">Your Name</label>
                <input
                  type="text"
                  data-testid="name-input"
                  className="w-full px-4 py-2 rounded-md bg-white/10 text-white focus:outline-none focus:ring-2 focus:ring-violet-500"
                  placeholder="Enter your name"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  required
                />
              </div>
              {activeTab === 'join' && (
                <div>
                  <label className="block text-sm text-violet-200 mb-1">Room ID</label>
                  <input
                    type="text"
                    data-testid="room-id-input"
                    className="w-full px-4 py-2 rounded-md bg-white/10 text-white focus:outline-none focus:ring-2 focus:ring-violet-500"
                    placeholder="Enter Room ID"
                    value={roomId}
                    onChange={(e) => setRoomId(e.target.value)}
                    required
                  />
                </div>
              )}
              <button
                type="submit"
                data-testid={activeTab === 'create' ? 'create-room-btn' : 'join-room-btn'}
                className={`w-full py-2 rounded-md font-semibold text-white ${
                  activeTab === 'create'
                    ? 'bg-green-500 hover:bg-green-600'
                    : 'bg-gradient-to-r from-purple-500 to-indigo-500 hover:opacity-90'
                }`}
              >
                {activeTab === 'create' ? '＋ Create Clipboard' : '👥 Join Clipboard'}
              </button>
            </form>
          </div>
        </div>

      </div>
    </div>
  );
};


export default HomePage;

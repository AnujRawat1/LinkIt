// Room.jsx
import React, { useState, useEffect } from 'react';
import CodeMirror from '@uiw/react-codemirror';
import { javascript } from '@codemirror/lang-javascript';
import { EditorView } from '@codemirror/view';
import { toast } from 'react-toastify';
import { ClipboardCopy, Share2 } from 'lucide-react';
import { useParams, useNavigate } from 'react-router-dom';
import useRoomContext from '../context/RoomContext';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import { baseURL_DEV } from '../config/AxiosHelper';
import { getContent, getParticipants, getFileNames, removeParticipant } from '../services/RoomService';

const Room = () => {
  const { roomId: urlRoomId } = useParams();
  const navigate = useNavigate();
  const { roomId, setRoomId, name, connected, setConnected, code, setCode } = useRoomContext();
  const [participants, setParticipants] = useState([]);
  const [addedFiles, setAddedFiles] = useState([]);
  const [stompClient, setStompClient] = useState(null);

  const activeRoomId = roomId || urlRoomId;

  useEffect(() => {
    setRoomId(activeRoomId); // Sync URL ID to context
  }, [urlRoomId]);

  // STOMP / SOCK JS CONNECTION
  useEffect(() => {
    const connectWebSocket = () => {
      const socket = new SockJS(`${baseURL_DEV}/clipboard`);
      const client = Stomp.over(socket);

      client.connect({}, () => {
        console.log('✅ Connected to STOMP');
        setStompClient(client);
        setConnected(true);

        client.subscribe(`/topic/room/${activeRoomId}`, (message) => {
          if (!message.body) {
            // Room deleted, redirect to homepage
            toast.info('Room has been closed.');
            navigate('/');
            return;
          }
          const newMessage = JSON.parse(message.body);
          setCode(newMessage.content || '');
          setParticipants(newMessage.participants || []);
        });
      }, (error) => {
        console.error('STOMP connection error:', error);
        setConnected(false);
      });

      // Handle WebSocket disconnection
      client.onDisconnect = () => {
        console.log('Disconnected from STOMP');
        setConnected(false);
      };
    };

    if (activeRoomId) {
      connectWebSocket();
    }

    return () => {
      if (stompClient && connected) {
        stompClient.disconnect();
      }
    };
  }, [activeRoomId]);

  // LOAD CONTENT
  useEffect(() => {
    async function loadContent() {
      try {
        const response1 = await getContent(activeRoomId);
        const response2 = await getParticipants(activeRoomId);
        setCode(response1.data || '');
        setParticipants(response2.data || []);
        console.log('Data Loaded');
      } catch (error) {
        console.error('Failed to load room content:', error);
        toast.error('Failed to load room content.');
      }
    }

    if (activeRoomId && connected) {
      loadContent();
    }
  }, [activeRoomId, connected]);

  // SEND MESSAGE
  const sendMessage = (val) => {
    if (stompClient && connected && val) {
      const message = {
        roomId: activeRoomId,
        content: val,
        participants: participants,
        fileNames: addedFiles,
        createdAt: new Date(),
      };
      stompClient.send(`/app/sendMessage/${activeRoomId}`, {}, JSON.stringify(message));
    } else {
      toast.error('Not connected or message is empty!');
    }
  };

  // REMOVE PARTICIPANT ON LEAVE
  const handleLeaveRoom = async () => {
    if (activeRoomId && name) {
      try {
        await removeParticipant(activeRoomId, name);
        localStorage.removeItem('name');
        localStorage.removeItem('roomId');
        console.log('✅ Participant removed:', name);
      } catch (error) {
        console.error('❌ Failed to remove participant:', error);
      }
    }
  };

  useEffect(() => {
    window.addEventListener('beforeunload', handleLeaveRoom);

    return () => {
      handleLeaveRoom(); // Call on unmount (navigation)
      window.removeEventListener('beforeunload', handleLeaveRoom);
    };
  }, [activeRoomId, name]);

  const handleCodeChange = (val) => {
    setCode(val);
    sendMessage(val);
  };

  const handleCopyCode = () => {
    navigator.clipboard.writeText(code).then(() => {
      toast.success('Code copied to clipboard!');
    });
  };

  const handleCopyRoomId = () => {
    if (activeRoomId) {
      navigator.clipboard.writeText(activeRoomId).then(() => {
        toast.success(`Room ID (${activeRoomId}) copied!`);
      });
    } else {
      toast.error('Room ID not available!');
    }
  };

  return (
    <div className="min-h-screen flex flex-col md:flex-row text-white bg-gradient-to-br from-[#110027] to-[#1e003f]">
      <div className="md:w-1/4 w-full p-6 bg-white/10 backdrop-blur-md border-r border-white/20">
        <h2 className="text-2xl font-bold mb-4">👥 Participants</h2>
        <ul data-testid="participants-list" className="space-y-2 mb-6">
          {participants.map((name, idx) => (
            <li key={idx} className="bg-white/10 p-2 rounded text-violet-200">
              {name}
            </li>
          ))}
        </ul>
        <button
          onClick={handleCopyRoomId}
          data-testid="copy-room-id-btn"
          className="w-full py-2 bg-blue-600 hover:bg-blue-700 rounded text-white text-sm flex items-center justify-center gap-2"
        >
          <Share2 size={16} /> Copy Room ID
        </button>
      </div>

      <div className="flex-1 p-4 md:p-8 relative">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-4">
          <div className="flex flex-col sm:flex-row sm:items-center gap-2">
            <h1 className="text-2xl font-semibold">📝 Collaborative Code Editor</h1>
            {activeRoomId && (
              <span className="text-sm bg-white/10 px-3 py-1 rounded-full text-violet-300 border border-violet-500">
                Room ID: <span data-testid="room-id-badge" className="font-mono font-semibold text-base">{activeRoomId}</span>
              </span>
            )}
          </div>
          <div className="flex flex-col sm:flex-row sm:items-center gap-3">
            <button
              onClick={handleCopyCode}
              data-testid="copy-code-btn"
              className="flex items-center gap-2 px-4 py-2 bg-violet-600 hover:bg-violet-700 text-white rounded-md"
            >
              <ClipboardCopy size={18} /> Copy Code
            </button>
          </div>
        </div>

        <div className="rounded-md overflow-hidden border border-white/10">
          <div data-testid="editor-container">
          <CodeMirror
            value={typeof code === 'string' ? code : ''}
            height="500px"
            extensions={[javascript(), EditorView.lineWrapping, EditorView.editable.of(true)]}
            onChange={handleCodeChange}
            theme="dark"
          />
          </div>
        </div>

        {addedFiles.length > 0 && (
          <div className="mt-8">
            <h2 className="text-xl font-semibold mb-4 text-violet-300">Added Files</h2>
            <div className="overflow-x-auto">
              <table className="min-w-full text-sm text-white border border-white/10 rounded overflow-hidden">
                <thead className="bg-white/10 text-violet-200">
                  <tr>
                    <th className="py-2 px-4 border-r border-white/10 text-left">S.No</th>
                    <th className="py-2 px-4 border-r border-white/10 text-left">File Name</th>
                    <th className="py-2 px-4 border-r border-white/10 text-left">Size</th>
                    <th className="py-2 px-4 text-left">Action</th>
                  </tr>
                </thead>
                <tbody>
                  {addedFiles.map((file, idx) => (
                    <tr key={idx} className="odd:bg-white/5">
                      <td className="py-2 px-4 border-r border-white/10">{idx + 1}</td>
                      <td className="py-2 px-4 border-r border-white/10">{file.name}</td>
                      <td className="py-2 px-4 border-r border-white/10">{file.size}</td>
                      <td className="py-2 px-4">
                        <a href={file.url} target="_blank" rel="noopener noreferrer" className="text-blue-400 hover:underline">
                          View
                        </a>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Room;
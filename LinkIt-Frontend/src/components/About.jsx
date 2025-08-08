import React from 'react';

const About = () => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-[#2e005f] to-[#110027] py-16 px-6 text-white">
      <div className="max-w-5xl mx-auto">
        {/* Title Section */}
        <div className="text-center mb-10">
          <h1 className="text-4xl sm:text-5xl font-extrabold text-violet-300 font-mono italic mb-4">
            About LinkIt
          </h1>
          <p className="text-lg sm:text-xl text-violet-200">
            <strong>LinkIt</strong> is your modern solution to a simple problem — syncing clipboard data across multiple devices in real-time.
            Whether you're copying code, links, or notes — LinkIt ensures it's available on all your devices without email or file transfer.
          </p>
        </div>

        {/* Feature Section */}
        <div className="grid sm:grid-cols-2 gap-6">
          <div className="bg-white/10 p-6 rounded-xl shadow-md backdrop-blur hover:shadow-lg transition duration-300">
            <h2 className="text-2xl font-semibold text-violet-200 mb-3">🚀 Why LinkIt?</h2>
            <ul className="list-disc list-inside text-white/90 space-y-2 text-base">
              <li>Instant clipboard syncing between devices</li>
              <li>Secure and private clipboard rooms</li>
              <li>No login or app install required</li>
              <li>Real-time code and text sharing</li>
              <li>User can also share files</li>
            </ul>
          </div>

          <div className="bg-white/10 p-6 rounded-xl shadow-md backdrop-blur hover:shadow-lg transition duration-300">
            <h2 className="text-2xl font-semibold text-violet-200 mb-3">🔧 Tech Stack</h2>
            <ul className="list-disc list-inside text-white/90 space-y-2 text-base">
              <li>React.js for frontend UI</li>
              <li>Tailwind CSS for styling</li>
              <li>Material UI components</li>
              <li>Future-ready for WebSocket and Cloud integration</li>
            </ul>
          </div>
        </div>

        {/* Footer */}
        <div className="mt-12 text-center">
          <p className="text-md sm:text-lg text-violet-300 italic">
            Crafted with 💙 to simplify your digital productivity.
          </p>
        </div>
      </div>
    </div>
  );
};

export default About;

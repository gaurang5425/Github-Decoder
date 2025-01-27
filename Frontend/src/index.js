import React from "react";
import ReactDOM from "react-dom/client"; // Import createRoot from React 18
import App from "./App"; // Main App component
import "./index.css"; // Global styles

// Create a root element and render the App component
const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
    <React.StrictMode>
        <App />
    </React.StrictMode>
);

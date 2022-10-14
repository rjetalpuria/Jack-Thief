import './App.css';
import {UserProvider} from "./hooks/User";
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import Home from "./pages/Home";
import Room from "./pages/Room";
import Time from "./pages/Time";

function App() {
    return (
        <UserProvider>
            <Router>
                <div className="App">
                    <Routes>
                        <Route exact path="/" element={<Home />} />
                        <Route path="/room" element={<Room/>}/>
                        <Route path="/time-sse" element={<Time/>}/>
                    </Routes>
                </div>
            </Router>

        </UserProvider>

    );
}

export default App;

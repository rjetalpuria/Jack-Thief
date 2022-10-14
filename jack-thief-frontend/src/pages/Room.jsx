import {useUser} from "../hooks/User";
import {useEffect, useState} from "react";
import {useSearchParams} from "react-router-dom";
import {shufflePile, userList} from "../backend/room";
import {getPile, pickCard, removePairs, resetGame, startGame} from "../backend/game";
import {useForm} from "react-hook-form";
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import logo from "../assets/logo.png";

const Room = () => {
    const [searchParams, setSearchParams] = useSearchParams();
    const {register, getValues, handleSubmit} = useForm();

    const {userId, setUserId} = useUser();
    const [roomId, setRoomId] = useState("");
    const [usersInRoom, setUsersInRoom] = useState([]);
    const [userCards, setUserCards] = useState([]);
    const [pickFrom, setPickFrom] = useState("");
    const [won, setWon] = useState(false);
    const [lost, setLost] = useState(false);

    useEffect(() => {
        setRoomId(searchParams.get("id"));
    }, [])

    useEffect(() => {
        if(roomId){
            getUserList(roomId);
        }
    }, [roomId])

    useEffect(() => {
        const eventSource = new EventSource("http://localhost:8080/subscribe")

        eventSource.addEventListener("update", (event) => {
            console.log("Update event occurred");
            const eventData = JSON.parse(event.data);
            console.log(JSON.stringify(eventData, null, 2));
            if(eventData.message === "update-cards" && (eventData.user === userId || eventData.user === "all")){
                console.log("Updating my pile")
                handleGetPile();
            } else if(eventData.message === "game-started"){
                console.log("New game started")
                setWon(false)
                setLost(false)
                handleGetPile()
            }else if(eventData.message === "update-userList"){
                console.log("Updating my userList");
                getUserList(searchParams.get("id"));
            } else if(eventData.message === "winner"){
                if(eventData.user === userId){
                    console.log("I win")
                    setWon(true);
                } else {
                    toast.info(eventData.user + " won!", {
                        position: toast.POSITION.TOP_RIGHT,
                        theme: "colored",
                        autoClose: 3000,
                        pauseOnFocusLoss: false
                    })
                }
            } else if(eventData.message === "loser"){
                if(eventData.user === userId){
                    console.log("I am JACK");
                    setLost(true);
                } else {
                    toast.info(eventData.user + " is the Jack Thief!!", {
                        position: toast.POSITION.TOP_RIGHT,
                        theme: "colored",
                        autoClose: 3000,
                        pauseOnFocusLoss: false
                    })
                }
            }

        })
    }, [])

    const getUserList = (roomId) => {
        userList(roomId)
            .then(response => {
                setUsersInRoom(response.data.users);
            })
            .catch(error => console.log(JSON.stringify(error, null, 2)));
    }

    const handleStart = () => {
        startGame(roomId)
            .then()
            .catch(error => {
                console.log(JSON.stringify(error, null, 2));
                toast.error(error.response.data, {
                    position: toast.POSITION.TOP_RIGHT,
                    theme: "colored",
                    autoClose: 3000,
                    pauseOnFocusLoss: false
                });
            })
    }

    const handleGetPile = () => {
        getPile(searchParams.get("id"), userId)
            .then((response) => {
                setUserCards(response.data.cards)
            })
            .catch(error => console.log(JSON.stringify(error, null, 2)))
    }

    const handleReset = () => {
        resetGame(roomId)
            .then()
            .catch(error => console.log(JSON.stringify(error, null, 2)))
    }

    const handleDuplicates = () => {
        removePairs(roomId, userId)
            .then(() => handleGetPile())
            .catch(error => console.log(JSON.stringify(error, null, 2)))
    }

    const handleDropdownChange = (event) => {
        setPickFrom(event.target.value);
    }

    const handleShuffle = () => {
        shufflePile(userId, roomId)
            .then(() => handleGetPile())
            .catch(error => console.log(JSON.stringify(error, null, 2)))
    }

    const handlePick = () => {
        if(!pickFrom || pickFrom === "--"){
            toast.warn("Select a user to pick from")
        } else{
            const cardNumber = getValues("cardNumber") - 1;
            pickCard(roomId, userId, pickFrom, cardNumber)
                .then(() => handleGetPile())
                .catch(error => {
                    console.log(JSON.stringify(error, null, 2));
                    toast.warn(error.response.data, {
                        position: toast.POSITION.TOP_RIGHT,
                        theme: "colored",
                        autoClose: 3000,
                        pauseOnFocusLoss: false
                    });
                })
        }
    }
    return(
        <div>
            <ToastContainer/>
            <img id="logo" src={logo} alt="JackThief Logo"></img>
            <br/>
            <div className="info-container">
                <strong className="info-name">Username: </strong>
                <p>{userId}</p>
            </div>
            <div className="info-container">
                <strong className="info-name">Room Id: </strong>
                <p>{roomId}</p>
            </div>
            <div>
                <strong className="info-name">Users In Room: </strong>
                {usersInRoom.map(user => <p key={user}>{user}</p>)}
            </div>

            <button id="start-btn" onClick={handleStart}>Start Game</button>
            {/*<button id="reset-btn" onClick={handleReset}>Reset Game</button>*/}
            <br/><br/>
            {!won && !lost &&
                <div>
                    <button id="pile-btn" onClick={handleGetPile}>Get Pile</button>
                    <button id="pair-btn" onClick={handleDuplicates}>Remove Pairs</button>
                    <button id="shuffle-btn" onClick={handleShuffle}>Shuffle Pile</button>
                    <br/><br/>
                    <strong>{"Pick from:"}</strong>
                    <select defaultValue={"--"} onChange={handleDropdownChange}>
                        <option value={"--"} key={"--"}>--</option>
                        {usersInRoom.map(user =>( <option value={user} key={user}>{user}</option>))
                        }
                    </select>
                    <strong> card#:</strong>
                    <input type="number" defaultValue="1" min="1" max="26" {...register("cardNumber")}/>
                    <button id="pick-btn" onClick={handlePick}>Pick!</button>
                    <br/><br/>
                    {userCards.length !== 0 &&
                        <div>
                            <strong className="info-name">Your Cards:</strong>
                            <br/><br/>
                            {/*{userCards.map(card => <p key={card.code}>{card.code}</p>)}*/}
                            {userCards.map(card => <img className="card-images"
                                                        src={"https://deckofcardsapi.com/static/img/"+card.code+".png"}
                                                        alt={card.code}
                                                        key={card.code}
                            />)}
                        </div>
                    }
                </div>
            }
            <br/><br/>
            {won &&
                <div>
                    <strong id="win-text">You win!</strong>
                </div>
            }
            {lost &&
                <div>
                    <strong id="lose-text">You are the JACK THIEF!!</strong>
                </div>
            }
        </div>
    );
}

export default Room;
import {useForm} from "react-hook-form";
import {createRoom, joinRoom} from "../backend/room";
import {useNavigate} from "react-router-dom";
import {useUser} from "../hooks/User";
import logo from "../assets/logo.png";
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const Home = () =>{
    const {register, getValues, handleSubmit} = useForm();
    const {userId, setUserId} = useUser();
    const navigate = useNavigate();
    const story1 = "There once lived a king and ruled on Mount Paramount. The king was very rich and fond of his jewelery collection. " +
        "One day he found out that some of his jewelry was missing. He suspects it was one of his jacks.";
    const story2 ="Play the game and find out which jack stole the king's jewelery.";
    const goal = "Remove as many pairs as possible and don't end up with a JACK in the end."
    const rules = ["Uses a deck of 51 cards. (One of the 4 JACKS is removed)",
        "Distribute all cards among all players face down",
        "Each player than looks at their cards and removes pairs. A pair consists of 2 cards with the same face/value",
        "Once everyone removes their pairs, the game begins.",
        "Each players takes turn picking a card from the player on their right without looking, and removes any pairs they find",
        "In the end, there will be only 1 card remaining -- a JACK; and the person bearing it becomes the jack thief."

    ]
    const create = () => {
        const usr = getValues("username")
        if(usr){
            createRoom(usr)
                .then(response => {
                    console.log(JSON.stringify(response.data, null, 2));
                    setUserId(usr);
                    navigate("/room/?id="+ response.data.roomIdStr)
                })
                .catch(error => console.log(JSON.stringify(error, null, 2)))
        } else {
            toast.error("No username provided", {
                position: toast.POSITION.TOP_RIGHT,
                theme: "colored",
                autoClose: 3000,
                pauseOnFocusLoss: false
            });
        }

    }
    const join = () => {
        const usr = getValues("username")
        const room = getValues("room-id")
        if(!usr){
            toast.error("No username provided", {
                position: toast.POSITION.TOP_RIGHT,
                theme: "colored",
                autoClose: 3000,
                pauseOnFocusLoss: false
            });
        } else if(!room){
            toast.error("Room ID not provided", {
                position: toast.POSITION.TOP_RIGHT,
                theme: "colored",
                autoClose: 3000,
                pauseOnFocusLoss: false
            });
        } else if(!room.match(/^[a-f\d]{24}$/i) ){
            toast.error("Room ID is invalid Object ID", {
                position: toast.POSITION.TOP_RIGHT,
                theme: "colored",
                autoClose: 3000,
                pauseOnFocusLoss: false
            });
        } else{
            joinRoom(usr, room)
                .then(response => {
                    console.log(JSON.stringify(response.data, null, 2))
                    setUserId(usr)
                    navigate("/room/?id="+ response.data.roomIdStr)
                })
                .catch(error => {
                    console.log(JSON.stringify(error, null, 2))
                    toast.error(error.response.data.message, {
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
            <img id="logo" src={logo} alt="JackThief Logo"></img>
            <ToastContainer/>
            <div className="game-info">
                <strong>STORY:</strong>
                <p>{story1}</p>
                <p>{story2}</p>
                <strong>GOAL:</strong>
                <p>{goal}</p>
                <strong id="rules">RULES:</strong>
                <ol>
                    {rules.map(rule => <li key={rule}>{rule}</li>)}
                </ol>
            </div>
            <br/>
            <form>
                <label>Enter Name: </label>
                <input id="name-input" type="text" placeholder="John Doe" {...register("username")}/>
                <br/><br/>
                <button onClick={handleSubmit(create)}>Create Room</button>
                <br/><br/>
                <h2 id="or">OR</h2>
                <br/><br/>
                <label>Enter Room Code: </label>
                <br/><br/>
                <input id="room-input" type="text" placeholder="a very very long code" {...register("room-id")}/>
                <br/><br/>
                <button onClick={handleSubmit(join)}>Join Room</button>
            </form>
            <br/><br/><br/>
        </div>
    );
}

export default Home;
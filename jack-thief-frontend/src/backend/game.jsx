import Axios from "axios";
import room from "../pages/Room";

export async function startGame(roomId){
    const options = {
        method: "GET",
        url: "http://localhost:8080/start-game/" + roomId
    }
    console.log("Axios url: ", options.url)
    return Axios.request(options)
}

export async function getPile(roomId, username){
    const payload = {
        "username": username
    }
    const options = {
        method: "POST",
        url: "http://localhost:8080/get-pile/" + roomId,
        data: payload
    }
    console.log("GET PILE URL: " + options.url)
    return Axios.request(options);
}

export async function resetGame(roomId){
    const options = {
        method: "GET",
        url: "http://localhost:8080/reset-game/" + roomId
    }
    console.log("Axios url: ", options.url)
    return Axios.request(options)
}

export async function removePairs(roomId, username){
    const payload = {
        "username": username
    }
    const options = {
        method: "POST",
        url: "http://localhost:8080/remove-pairs/" + roomId,
        data: payload
    }
    return Axios.request(options);
}

export async function pickCard(roomId, username, pickFrom, cardNumber){
    const payload = {
        "username": username,
        "fromUser": pickFrom,
        "cardNumber": cardNumber
    }
    const options = {
        method: "POST",
        url: "http://localhost:8080/pick-card/" + roomId,
        data: payload
    }
    return Axios.request(options);
}
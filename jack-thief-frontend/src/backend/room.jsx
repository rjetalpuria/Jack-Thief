import Axios from "axios";

export async function createRoom(username){
    const payload = {
        "username": username
    }
    const options = {
        method: "POST",
        url: "http://localhost:8080/create-room/",
        data: payload
    }
    return Axios.request(options)
}

export async function joinRoom(username, roomId){
    const payload = {
        "username": username
    }
    const options = {
        method: "POST",
        url: "http://localhost:8080/join-room/" + roomId,
        data: payload
    }
    return Axios.request(options)
}

export async function userList(roomId){
    const options = {
        method: "GET",
        url: "http://localhost:8080/user-list/" + roomId
    }
    return Axios.request(options)
}

export async function shufflePile(username, roomId){
    const payload = {
        "username": username
    }
    const options = {
        method: "POST",
        url: "http://localhost:8080/shuffle-pile/" + roomId,
        data: payload
    }
    return Axios.request(options)
}
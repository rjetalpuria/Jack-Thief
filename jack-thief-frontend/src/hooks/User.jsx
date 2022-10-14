import React, {createContext, useState, useContext} from "react";
const localStorage = require("local-storage");

const UserContext = createContext({});

export const UserProvider = ({children}) => {
    const [userId, userIdSetter] = useState(localStorage.get("userId"));

    const setUserId = (userId) => {
        userIdSetter(userId);
        localStorage.set("userId", userId);
    }

    const value = {
        userId, setUserId
    }

    return (
        <UserContext.Provider value={value}>
            {children}
        </UserContext.Provider>
    )
}

export const useUser = () => {
    return useContext(UserContext);
}
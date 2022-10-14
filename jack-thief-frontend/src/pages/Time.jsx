import {useEffect, useState} from "react";

const Time = () => {
    useEffect(() => {
        var url = "http://localhost:8080/subscribe"
        var eventSource = new EventSource(url)

        eventSource.addEventListener("update", (event) => {
            console.log("Update event occurred")
            console.log(JSON.stringify(event.data, null, 2));
        })
    }, [])

    return(
        <div>
            <h4>Received Data</h4>
        </div>
    );
}

export default Time;
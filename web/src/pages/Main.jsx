import React, { useState } from "react";
import { GoogleMap, LoadScript, Marker } from "@react-google-maps/api";
import firebase from "firebase/compat/app";
import "firebase/compat/database";
import "./style.css";
const firebaseConfig = {
  apiKey: "YOUR_VALUE",
  authDomain: "YOUR_VALUE",
  databaseURL: "YOUR_VALUE",
  projectId: "YOUR_VALUE",
  storageBucket: "YOUR_VALUE",
  messagingSenderId: "YOUR_VALUE",
  appId: "YOUR_VALUE",
};

firebase.initializeApp(firebaseConfig);


const Map = () => {
  const [center, setCenter] = useState({ lat: 51.16052269999999, lng: 71.4703558 });
  const [marker, setMarker] = useState({ lat: 0, lng: 0 });
  const [address, setAddress] = useState("");
  const [flat, setInput1] = useState("");
  const [flong, setInput2] = useState("");
  const [code, setInput3] = useState("");
  const [email, setInput4] = useState("");

  const handleAddressChange = (event) => {
    setAddress(event.target.value);
  };

  const handleSearch = () => {
    const geocoder = new window.google.maps.Geocoder();
    const city = "ASTANA";
    const addressWithCity = `${address}, ${city}`;
    geocoder.geocode({ address: addressWithCity }, (results, status) => {
      if (status === "OK") {
        const { lat, lng } = results[0].geometry.location;
        setCenter({ lat: lat(), lng: lng() });
        setMarker({ lat: lat(), lng: lng() });
      }
    });
  };
  const handleClick = (event) => {
    setMarker({ lat: event.latLng.lat(), lng: event.latLng.lng() });
  };
  const handleInput1Change = (event) => {
    setInput1(event.target.value);
  };

  const handleInput2Change = (event) => {
    setInput2(event.target.value);
  };

  const handleInput3Change = (event) => {
    setInput3(event.target.value);
  };
  const handleInput4Change = (event) => {
    setInput4(event.target.value);
  };
  const handleSendData = () => {
    const database = firebase.database();
    database.ref("flat").set(parseFloat(flat));
    database.ref("flong").set(parseFloat(flong));
    database.ref("code").set(parseFloat(code));
    database.ref("email").set(email);
  }; 
  return (
    <div className="container">
  <div className="form-container">
  <span className="span">Enter the address:</span>
  <div className="lines">
    <input type="text" value={address} onChange={handleAddressChange} />
    </div>
    <button className="button" onClick={handleSearch}>Search</button>
    <span className="span">Enter the final latitude:</span>
    <div className="lines">
      <input type="number" value={flat} onChange={handleInput1Change} />
    </div>
    <span className="span">Enter the final longitude:</span>
    <div className="lines"> 
      <input type="number" value={flong} onChange={handleInput2Change} />
    </div>
    <span className="span">Enter the lock code:</span>
    <div className="lines">
      <input type="number" value={code} onChange={handleInput3Change} />
    </div>
    <span className="span">Enter the email:</span>
    <div className="lines">
      <input type="text" value={email} onChange={handleInput4Change} />
    </div>
    <button className="button" onClick={handleSendData}>Send Data</button>
  </div>
  <div className="map-container">
    <LoadScript googleMapsApiKey="YOUR_VALUE">
      <GoogleMap
        mapContainerStyle={{ height: "400px" }}
        center={center}
        zoom={15}
        onClick={handleClick}
      >
        <Marker position={marker} />
      </GoogleMap>
    </LoadScript>
    <div>Latitude: {marker.lat}</div>
    <div>Longitude: {marker.lng}</div>
    <div>Final latitude: {flat}</div>
    <div>Final Longitude: {flong}</div>
    <div>Lock code: {code}</div>
    <div>Email: {email}</div>
  </div>
</div>
  );
};

export default Map;

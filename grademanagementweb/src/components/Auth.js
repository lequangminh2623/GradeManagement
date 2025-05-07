import { BrowserRouter, Route, Routes } from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.min.css';
import { Container } from "react-bootstrap";
import { useReducer } from "react";
import { MyDispatcherContext, MyUserContext } from "../configs/MyContexts";
import Login from "./Login";
import Register from "./Register";
import MyUserReducer from "../reducers/MyUserReducer";

const Auth = () => {
  const [user, dispatch] = useReducer(MyUserReducer, null);

  return (
    <MyUserContext.Provider value={user}>
      <MyDispatcherContext.Provider value={dispatch}>
        <BrowserRouter>
          <Container>
            <Routes>
              <Route path="/register" element={<Register />} />
              <Route path="/login" element={<Login />} />
            </Routes>
          </Container>
        </BrowserRouter>
    </MyDispatcherContext.Provider>
    </MyUserContext.Provider>
  );
}

export default Auth;
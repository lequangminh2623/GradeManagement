import 'bootstrap/dist/css/bootstrap.min.css';
import { BrowserRouter, Route, Routes, Navigate, useLocation } from "react-router-dom";
import Footer from "./components/layouts/Footer";
import Header from "./components/layouts/Header";
import Home from "./components/Home";
import Classrooms from './components/Classrooms';
import Login from './components/Login';
import { Container } from 'react-bootstrap';
import { MyDispatcherContext, MyUserContext } from "./configs/MyContexts";
import { useReducer } from "react";
import MyUserReducer from "./reducers/MyUserReducer";
import Register from './components/Register';
import ClassroomDetail from './components/ClassroomDetail';
import RegisterGoogle from './components/RegisterGoogle';

const App = () => {
  const [user, dispatch] = useReducer(MyUserReducer, null);

  return (
    <MyUserContext.Provider value={user}>
      <MyDispatcherContext.Provider value={dispatch}>
        <BrowserRouter>
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />

            <Route
              path="*"
              element={
                <>
                  <Header />
                  <div style={{ paddingTop: "70px" }}>
                    <Container>
                      <Routes>
                        <Route path="/" element={user ? <Home /> : <Navigate to="/login" />} />
                        <Route path="/classrooms" element={user ? <Classrooms /> : <Navigate to="/login" />} />
                        <Route
                          path="/classrooms/:classroomId" element={user ? <ClassroomDetail /> : <Navigate to="/login" />} />
                      </Routes>
                    </Container>
                  </div>
                  <Footer />
                </>
              }
            />
          </Routes>
        </BrowserRouter>
      </MyDispatcherContext.Provider>
    </MyUserContext.Provider>
  );
}

export default App;
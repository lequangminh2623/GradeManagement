import 'bootstrap/dist/css/bootstrap.min.css';
import { BrowserRouter, Route, Routes, Navigate } from "react-router-dom";
import Footer from "./components/layouts/Footer";
import Header from "./components/layouts/Header";
import Home from "./components/Home";
import Classrooms from './components/Classrooms';
import Login from './components/Login';
import { Container } from 'react-bootstrap';
import { MyDispatcherContext, MyUserContext } from "./configs/MyContexts";
import { useEffect, useReducer } from "react";
import MyUserReducer from "./reducers/MyUserReducer";
import Register from './components/Register';
import ClassroomDetail from './components/ClassroomDetail';
import ChatBox from './components/ChatBox';
import Grade from './components/Grade';
import Forum from './components/Forum';
import CreatePost from './components/CreatePost';
import ForumPostDetail from './components/ForumPostDetail';


const App = () => {
  const initialUser = JSON.parse(localStorage.getItem("user"));
  const [user, dispatch] = useReducer(MyUserReducer, initialUser);

  useEffect(() => {
    if (user) {
      localStorage.setItem("user", JSON.stringify(user));
    } else {
      localStorage.removeItem("user");
    }
  }, [user]);

  return (
    <MyUserContext.Provider value={user}>
      <MyDispatcherContext.Provider value={dispatch}>
        <BrowserRouter>
          <Routes>
            <Route path="/login" element={user ? <Navigate to="/" /> : <Login />} />
            <Route path="/register" element={user ? <Navigate to="/" /> : <Register />} />

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
                        <Route path="/classrooms/:classroomId"
                          element={
                            user ? (user.role === "ROLE_LECTURER" ? <ClassroomDetail /> : <Navigate to='/' />) : <Navigate to='/login' />}
                        />
                        <Route path="/classrooms/:classroomId/forums"
                          element={
                            user ? (user.role === "ROLE_ADMIN" ? <Navigate to='/' /> : <Forum />) : <Navigate to='/login' />}
                        >
                          <Route path="add"
                            element={
                              user ? (user.role === "ROLE_ADMIN" ? <Navigate to='/' /> : <CreatePost />) : <Navigate to='/login' />} />
                        </Route>

                        <Route path="/forum/:postId" element={
                          user ? (user.role === "ROLE_ADMIN" ? <Navigate to='/' /> : <ForumPostDetail />) : <Navigate to='/login' />}
                        />

                        <Route path="/chatbox" element={user ? <ChatBox /> : <Navigate to="/login" />} />
                        <Route path="/grades"
                          element={
                            user ? (user.role === "ROLE_STUDENT" ? <Grade /> : <Navigate to='/' />) : <Navigate to='/login' />
                          } />
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
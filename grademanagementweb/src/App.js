import 'bootstrap/dist/css/bootstrap.min.css';
import { BrowserRouter, Route, Routes } from "react-router-dom";
import Footer from "./components/layouts/Footer";
import Header from "./components/layouts/Header";
import Home from "./components/Home";
import Classrooms from './components/Classrooms';
import { Container } from 'react-bootstrap';
import { MyDispatcherContext, MyUserContext } from "./configs/MyContexts";
import { useReducer } from "react";
import MyUserReducer from "./reducers/MyUserReducer";

const App = () => {

  const [user, dispatch] = useReducer(MyUserReducer, null);

  return (
     <MyUserContext.Provider value={user}>
      <MyDispatcherContext.Provider value={dispatch}>
        <BrowserRouter>
          <Header />
        
          <Container>
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/classrooms" element={<Classrooms />} />
            </Routes>
          </Container>

          <Footer />
        </BrowserRouter>
      </MyDispatcherContext.Provider>
    </MyUserContext.Provider>
  );
}

export default App;
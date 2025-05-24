import { useContext, useState } from "react";
import { Button, Container, Form, Image, InputGroup, Nav, Navbar, NavDropdown } from "react-bootstrap";
import { Link, useNavigate, useLocation } from "react-router-dom";
import { MyDispatcherContext, MyUserContext } from "../../configs/MyContexts";
import { FaRegUser } from "react-icons/fa6";
import { FaSearch } from "react-icons/fa";
import { useTranslation } from "react-i18next";

const Header = () => {
  const user = useContext(MyUserContext);
  const dispatch = useContext(MyDispatcherContext);
  const [kw, setKw] = useState("");
  const navigate = useNavigate();
  const location = useLocation();
  const { i18n, t } = useTranslation();

  const changeLanguage = (lng) => {
    i18n.changeLanguage(lng);
    localStorage.setItem('language', lng);
  }

  const search = (e) => {
    e.preventDefault();
    const params = new URLSearchParams(location.search);
    const trimmed = kw.trim();

    if (trimmed) {
      params.set('kw', trimmed);
    } else {
      params.delete('kw');
    }

    navigate({
      pathname: location.pathname,
      search: params.toString(),
    });
  };

  return (
    <Navbar expand="xl" bg="white" className="shadow-sm bg-body-tertiary fixed-top">
      <Container>
        <Navbar.Brand as={Link} to="/">
          <h2 className="text-primary mb-0">Grade</h2>
        </Navbar.Brand>
        <Navbar.Toggle aria-controls="navbar-nav" />

        <Navbar.Collapse id="navbar-nav">
          <Nav className="m-auto my-2 my-lg-0" navbarScroll>
            <Link to="/" className="nav-link">
              {t('home')}
            </Link>

            {(user && user.role !== "ROLE_ADMIN") && <>
              <Link to="/classrooms" className="nav-link">
                {t('classrooms')}
              </Link>

              {(user.role === "ROLE_STUDENT") &&
                <Link to="/grades" className="nav-link">
                  {t('grades')}
                </Link>}

              {(user.role === "ROLE_LECTURER") &&
                <Link to="/statistics" className="nav-link">
                  {t('statistics')}
                </Link>}

              <Link to="/chatbox" className="nav-link">
                {t('chat')}
              </Link>
            </>}
          </Nav>

          <Form onSubmit={search} className="d-flex">
            <InputGroup>
              <Form.Control
                type="search"
                value={kw}
                onChange={(e) => setKw(e.target.value)}
                placeholder={t('search')}
              />
              <Button type="submit">
                <FaSearch />
              </Button>
            </InputGroup>
          </Form>

          {user ? (
            <NavDropdown
              align="end"
              title={
                <Image
                  src={user.avatar}
                  roundedCircle
                  style={{ width: '40px', height: '40px' }}
                />
              }
              id="user-dropdown"
              className="ms-4"
              style={{ color: "#0d6efd" }}
            >
              <div className="text-center px-3 py-2">
                <Image
                  src={user.avatar}
                  roundedCircle
                  style={{ width: '80px', height: '80px' }}
                />
                <p className="mt-2 mb-1 fw-bold">
                  {`${user.lastName} ${user.firstName}`}
                </p>
              </div>

              <NavDropdown.Item as={Link} to="/profile">
                {t('account')}
              </NavDropdown.Item>

              <NavDropdown.ItemText>
                <Form.Label>{t('select_language')}:</Form.Label>

                <Form.Select
                  size="sm"
                  value={i18n.language}
                  onChange={(e) => changeLanguage(e.target.value)}
                >
                  <option value="vi">Tiếng Việt</option>
                  <option value="en">English</option>
                </Form.Select>
              </NavDropdown.ItemText>


              <NavDropdown.Divider />
              
              <NavDropdown.Item onClick={() => dispatch({ type: 'logout' })} className="text-danger">
                {t('logout')}
              </NavDropdown.Item>

            </NavDropdown>
          ) : (
            <Link to="/login" className="text-dark ms-4">
              <FaRegUser size={25} />
            </Link>
          )}
        </Navbar.Collapse>
      </Container>
    </Navbar >
  );
};

export default Header;

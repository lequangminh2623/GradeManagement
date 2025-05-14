import { use, useContext, useState } from "react";
import { Button, Container, Form, Image, InputGroup, Nav, Navbar, NavDropdown } from "react-bootstrap";
import { Link, useNavigate, useLocation } from "react-router-dom";
import { MyDispatcherContext, MyUserContext } from "../../configs/MyContexts";
import { FaRegUser } from "react-icons/fa6";
import { FaSearch, FaUserCircle } from "react-icons/fa";

const Header = () => {
  const user = useContext(MyUserContext);
  const dispatch = useContext(MyDispatcherContext);
  const [kw, setKw] = useState("");
  const navigate = useNavigate();
  const location = useLocation();

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
              Trang chủ
            </Link>

            <Link to="/classrooms" className="nav-link">
              Lớp học
            </Link>

            {(user && user.role === "ROLE_STUDENT") &&
              <Link to="/grades" className="nav-link">
                Điểm
              </Link>}

            <Link to="/chatbox" className="nav-link">
              Tin nhắn
            </Link>

            <NavDropdown title="Tiện ích" className="text-secondary">
              <NavDropdown.Item as={Link} to="/">
                ABC
              </NavDropdown.Item>
            </NavDropdown>
          </Nav>

          <Form onSubmit={search} className="d-flex">
            <InputGroup>
              <Form.Control
                type="search"
                value={kw}
                onChange={(e) => setKw(e.target.value)}
                placeholder="Tìm kiếm..."
              />
              <Button type="submit">
                <FaSearch />
              </Button>
            </InputGroup>
          </Form>

          {user ? (
            <NavDropdown
              align="end"
              title={<Image
                src={user.avatar}
                roundedCircle
                style={{ width: '40px', height: '40px' }}
              />}
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
                Quản lý tài khoản
              </NavDropdown.Item>
              <NavDropdown.Divider />
              <NavDropdown.Item onClick={() => dispatch({ type: 'logout' })}>
                Đăng xuất
              </NavDropdown.Item>
            </NavDropdown>
          ) : (
            <Link to="/login" className="text-dark ms-4">
              <FaRegUser size={25} />
            </Link>
          )}
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default Header;

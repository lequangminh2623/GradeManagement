import { useContext } from "react";
import { Container, Image, Nav, Navbar, NavDropdown } from "react-bootstrap";
import { Link } from "react-router-dom";
import { MyDispatcherContext, MyUserContext } from "../../configs/MyContexts";
import { FaRegUser } from "react-icons/fa6";


const Header = () => {
    const user = useContext(MyUserContext)
    const dispatch = useContext(MyDispatcherContext);

    return (
        <Navbar expand="xl" bg="white" className="shadow-sm bg-body-tertiary fixed-top">
            <Container>
                <Navbar.Brand as={Link} to="/">
                    <h2 className="text-primary mb-0">Grade</h2>
                </Navbar.Brand>
                <Navbar.Toggle aria-controls="navbarScroll" />

                <Navbar.Collapse id="navbar-nav">
                    <Nav className="m-auto my-2 my-lg-0"
                        style={{ maxHeight: '100px' }}
                        navbarScroll>
                        <Link to="/" className="nav-link">Trang chủ</Link>
                        <Link to="/classrooms" className="nav-link">Lớp học</Link>

                        <NavDropdown title="Tiện ích" className="text-secondary">
                            <NavDropdown.Item as={Link} to="/">ABC</NavDropdown.Item>
                        </NavDropdown>

                    </Nav>

                    {user ? (
                        <NavDropdown
                            align="end"
                            title={<FaRegUser size={25} color="#0d6efd" />}
                            id="user-dropdown"
                        >
                            <div className="text-center px-3 py-2">
                                <Image
                                    src={user.avatar}
                                    roundedCircle
                                    style={{ width: "80px", height: "80px" }}
                                />
                                <p className="mt-2 mb-1 fw-bold">{`${user.lastName}${user.firstName}`}</p>
                            </div>
                            <NavDropdown.Item as={Link} to="/profile">Quản lý tài khoản</NavDropdown.Item>
                            <NavDropdown.Divider />
                            <NavDropdown.Item onClick={() => dispatch({ "type": "logout" })}>Đăng xuất</NavDropdown.Item>
                        </NavDropdown>
                    ) : (
                        <Link to="/login" className="text-dark">
                            <FaRegUser size={25} />
                        </Link>
                    )}
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

export default Header;
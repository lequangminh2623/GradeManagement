import { useContext, useState } from "react";
import { Alert, Button, Form, Container, Row, Col, Card } from "react-bootstrap";
import Apis, { authApis, endpoints } from "../configs/Apis";
import MySpinner from "./layouts/MySpinner";
import { Link, useNavigate } from "react-router-dom";
import cookie from 'react-cookies';
import { MyDispatcherContext } from "../configs/MyContexts";
import { FcGoogle } from "react-icons/fc";

const Login = () => {
    const info = [
        { title: "Email", field: "email", type: "email" },
        { title: "Mật khẩu", field: "password", type: "password" }
    ];
    const [user, setUser] = useState({});
    const [msg, setMsg] = useState();
    const [loading, setLoading] = useState(false);
    const nav = useNavigate();
    const dispatch = useContext(MyDispatcherContext);

    const setState = (value, field) => {
        setUser({ ...user, [field]: value });
    };

    const login = async (e) => {
        e.preventDefault();
        try {
            setLoading(true);

            let res = await Apis.post(endpoints['login'], {
                ...user
            });
            cookie.save('token', res.data.token);

            let u = await authApis().get(endpoints['profile']);

            dispatch({
                type: "login",
                payload: u.data
            });

            nav("/");
        } catch (ex) {
            console.error(ex);
            setMsg("Đăng nhập thất bại. Vui lòng kiểm tra thông tin!");
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container className="d-flex justify-content-center align-items-center" style={{ minHeight: "100vh" }}>
            <Row className="w-75">
                <Col md={{ span: 6, offset: 3 }}>
                    <Card className="shadow rounded-4">
                        <Card.Body>
                            <h1 className="text-center text-primary mt-1">Đăng nhập</h1>

                            {msg && <Alert variant="danger">{msg}</Alert>}

                            <Form onSubmit={login}>
                                {info.map(i => (
                                    <Form.Group className="mt-3" key={i.field}>
                                        <Form.Label>{i.title}</Form.Label>
                                        <Form.Control
                                            value={user[i.field] || ""}
                                            onChange={e => setState(e.target.value, i.field)}
                                            type={i.type}
                                            placeholder={i.title}
                                            required
                                        />
                                    </Form.Group>
                                ))}

                                <div className="d-grid gap-2 mt-4">
                                    {loading ? <MySpinner /> :
                                        <Button type="submit" variant="primary">
                                            Đăng nhập
                                        </Button>
                                    }
                                </div>
                            </Form>

                            <hr className="my-4" />

                            <div className="d-grid gap-2">
                                <Button className="d-flex justify-content-center align-items-center" variant="outline-secondary"
                                    onClick={() => console.log('google')}>
                                    <FcGoogle className="me-1" /> Đăng nhập với Google
                                </Button>

                                <div className="text-center">
                                    Chưa có tài khoản? <Link to="/register" style={{ textDecoration: 'none' }}>Đăng ký</Link>
                                </div>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default Login;
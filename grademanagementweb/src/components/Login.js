import { useContext, useEffect, useState } from "react";
import { Alert, Button, Form, Container, Row, Col, Card } from "react-bootstrap";
import Apis, { authApis, endpoints } from "../configs/Apis";
import MySpinner from "./layouts/MySpinner";
import { Link, useLocation, useNavigate } from "react-router-dom";
import cookie from 'react-cookies';
import { MyDispatcherContext } from "../configs/MyContexts";
import GoogleLoginButton from "./GoogleLoginButton";

const Login = () => {
    const info = [
        { title: "Email", field: "email", type: "email" },
        { title: "Mật khẩu", field: "password", type: "password" }
    ];
    const [user, setUser] = useState({});
    const [msg, setMsg] = useState();
    const [loading, setLoading] = useState(false);
    const [fieldErrors, setFieldErrors] = useState({});
    const nav = useNavigate();
    const dispatch = useContext(MyDispatcherContext);
    const location = useLocation()

    const setState = (value, field) => {
        setUser({ ...user, [field]: value });
    };

    const validate = () => {
        for (let i of info) {
            if (!(i.field in user) || user[i.field] === '') {
                setFieldErrors({ [i.field]: `${i.title} không được để trống` })
                return false
            }
        }

        return true
    }

    const login = async (e) => {
        e.preventDefault();
        setMsg('')
        setFieldErrors({})

        if (validate() === true) {
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
                if (ex.response?.status === 401) {
                    const errs = ex.response.data;
                    setMsg(errs);
                } else {
                    setMsg("Lỗi hệ thống hoặc kết nối.");
                }
            } finally {
                setLoading(false);
            }
        }
    }

    useEffect(() => {
        if (location.state?.success === true) {
            alert("Đăng ký tài khoản thành công")
        }
    }, [location.state?.success])

    return (
        <Container className="d-flex justify-content-center align-items-center" style={{ minHeight: "100vh" }}>
            <Row className="w-75">
                <Col md={{ span: 12, offset: 0 }}>
                    <Row className="align-items-center">
                        <Col md={6}>
                            <div>
                                <p className="text-primary display-2"><strong>Grade</strong></p>
                                <h4>Hệ thống quản lý điểm sinh viên</h4>
                            </div>
                        </Col>
                        <Col md={6}>
                            <Card className="shadow rounded-4">
                                <Card.Body>
                                    <h1 className="text-center text-primary mt-1">Đăng nhập</h1>

                                    {msg && <Alert variant="danger">{msg}</Alert>}

                                    <Form onSubmit={login}>
                                        {info.map((i) => (
                                            <Form.Group className="mt-3" key={i.field}>
                                                <Form.Label>{i.title}</Form.Label>
                                                <Form.Control
                                                    type={i.type}
                                                    placeholder={i.title}
                                                    value={user[i.field] || ""}
                                                    onChange={e => setState(e.target.value, i.field)}
                                                    isInvalid={!!fieldErrors[i.field]}
                                                />
                                                <Form.Control.Feedback type="invalid">
                                                    {fieldErrors[i.field]}
                                                </Form.Control.Feedback>
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
                                        <GoogleLoginButton setMsg={setMsg} />

                                        <div className="text-center">
                                            Chưa có tài khoản? <Link to="/register" style={{ textDecoration: 'none' }}>Đăng ký</Link>
                                        </div>
                                    </div>
                                </Card.Body>
                            </Card>
                        </Col>
                    </Row>
                </Col>
            </Row>
        </Container>
    );
};

export default Login;
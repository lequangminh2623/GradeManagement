import { useRef, useState } from "react";
import { Alert, Button, Card, Col, Container, Form, Row } from "react-bootstrap";
import Apis, { endpoints } from "../configs/Apis";
import MySpinner from "./layouts/MySpinner";
import { Link, useNavigate } from "react-router-dom";
import { FcGoogle } from "react-icons/fc";

const Register = () => {
    const info = [{
        title: "Tên",
        field: "firstName",
        type: "text"
    }, {
        title: "Họ và tên lót",
        field: "lastName",
        type: "text"
    }, {
        title: "Email",
        field: "email",
        type: "email"
    }, {
        title: "Mật khẩu",
        field: "password",
        type: "password"
    }, {
        title: "Xác nhận mật khẩu",
        field: "confirm",
        type: "password"
    }];
    const [user, setUser] = useState({});
    const avatar = useRef();
    const [msg, setMsg] = useState();
    const [loading, setLoading] = useState(false);
    const nav = useNavigate();

    const setState = (value, field) => {
        setUser({ ...user, [field]: value });
    }

    const register = async (e) => {
        e.preventDefault();
        if (user.password !== user.confirm) {
            setMsg("Mật khẩu KHÔNG khớp");
        } else {
            let form = new FormData();
            for (let key in user) {
                if (key !== 'confirm')
                    form.append(key, user[key]);
            }

            form.append("avatar", avatar.current.files[0]);
            try {
                setLoading(true);
                await Apis.post(endpoints['register'], form, {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                });

                nav("/login");
            } catch (ex) {
                console.error(ex);
            } finally {
                setLoading(false);
            }
        }
    }

    return (
        <Container className="d-flex justify-content-center align-items-center p-3" style={{ minHeight: "100vh" }}>
            <Row className="w-75">
                <Col md={{ span: 6, offset: 3 }}>
                    <Card className="shadow rounded-4">
                        <Card.Body>
                            <h1 className="text-center text-primary mt-1">Đăng ký tài khoản</h1>

                            {msg && <Alert variant="danger">{msg}</Alert>}

                            <Form onSubmit={register}>
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

                                <Form.Group className="mt-3">
                                    <Form.Label>Avatar</Form.Label>
                                    <Form.Control ref={avatar} type="file" placeholder="Ảnh đại diện" required />
                                </Form.Group>

                                <div className="d-grid gap-2 mt-4">
                                    {loading ? <MySpinner /> :
                                        <Button type="submit" variant="primary">
                                            Đăng ký
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
                                    Đã có tài khoản? <Link to="/login" style={{ textDecoration: 'none' }}>Đăng nhập</Link>
                                </div>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>

    )
}

export default Register;
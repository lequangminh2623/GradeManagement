import { useEffect, useRef, useState } from "react";
import { Alert, Button, Card, Col, Container, Form, Row } from "react-bootstrap";
import Apis, { endpoints } from "../configs/Apis";
import MySpinner from "./layouts/MySpinner";
import { Link, useLocation, useNavigate } from "react-router-dom";
import GoogleLoginButton from "./GoogleLoginButton";

const Register = () => {
    const location = useLocation();
    const isGoogleRegister = !!location.state?.newUser;
    const [user, setUser] = useState(location.state?.newUser || {});
    const avatar = useRef();
    const [msg, setMsg] = useState();
    const [fieldErrors, setFieldErrors] = useState({});
    const [loading, setLoading] = useState(false);
    const nav = useNavigate();

    const info = [
        {
            title: "Tên",
            field: "firstName",
            type: "text",
            disabled: isGoogleRegister
        }, {
            title: "Email",
            field: "email",
            type: "email",
            disabled: isGoogleRegister
        }, {
            title: "Họ và tên lót",
            field: "lastName",
            type: "text",
            disabled: isGoogleRegister
        }, {
            title: "Mật khẩu",
            field: "password",
            type: "password",
            disabled: false
        }, {
            title: "Mã số sinh viên",
            field: "code",
            type: "text",
            disabled: false
        }, {
            title: "Xác nhận mật khẩu",
            field: "confirm",
            type: "password",
            disabled: false
        }
    ];

    const setState = (value, field) => {
        setUser({ ...user, [field]: value });
    };

    const validate = () => {
        for (let i of info) {
            if (!(i.field in user) || user[i.field] === '') {
                setFieldErrors({ [i.field]: `${i.title} không được để trống` });
                return false;
            }
        }

        if (!avatar.current?.files[0]) {
            setFieldErrors({ avatar: "Cần có ảnh đại diện" });
            return false;
        }

        if (user.password !== user.confirm) {
            setFieldErrors({ confirm: `Mật khẩu không khớp!` });
            return false;
        }

        return true;
    };

    const register = async (e) => {
        e.preventDefault();
        setMsg('');
        setFieldErrors({});

        if (validate() === true) {
            try {
                setLoading(true);

                let form = new FormData();
                for (let key in user) {
                    if (key !== 'confirm') form.append(key, user[key]);
                }

                form.append("file", avatar.current.files[0]);

                await Apis.post(endpoints['users'], form, {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                });

                nav("/login", { state: { success: true } });
            } catch (ex) {
                if (ex.response?.status === 400 && Array.isArray(ex.response.data)) {
                    const errs = {};
                    ex.response.data.forEach(err => {
                        errs[err.field] = err.message;
                    });

                    setFieldErrors(errs);
                } else {
                    setMsg("Lỗi hệ thống hoặc kết nối.");
                }
            } finally {
                setLoading(false);
            }
        }
    }

    useEffect(() => {
        if (location.state?.newUser) {
            setUser(location.state.newUser)
        }
    }, [location.state?.newUser]);

    return (
        <Container className="d-flex justify-content-center align-items-center p-3" style={{ minHeight: "100vh" }}>
            <Row className="w-100">
                <Col md={{ span: 6, offset: 3 }}>
                    <p className="text-primary display-3 text-center"><strong>Grade</strong></p>

                    <Card className="shadow rounded-4">
                        <Card.Body>
                            <h1 className="text-center text-primary mt-1">
                                {isGoogleRegister ? "Hoàn tất đăng ký Google" : "Đăng ký tài khoản"}
                            </h1>

                            {msg && <Alert variant="danger">{msg}</Alert>}

                            <Form onSubmit={register}>
                                <Row>
                                    {info.map((i) => (
                                        <Col md={6} key={i.field} className="mt-3">
                                            <Form.Group controlId={i.field}>
                                                <Form.Label>{i.title}</Form.Label>
                                                <Form.Control
                                                    type={i.type}
                                                    placeholder={i.title}
                                                    value={user[i.field] || ""}
                                                    onChange={e => setState(e.target.value, i.field)}
                                                    disabled={i.disabled}
                                                    isInvalid={!!fieldErrors[i.field]}
                                                />
                                                <Form.Control.Feedback type="invalid">
                                                    {fieldErrors[i.field]}
                                                </Form.Control.Feedback>
                                            </Form.Group>
                                        </Col>
                                    ))}
                                </Row>

                                <Form.Group className="mt-3">
                                    <Form.Label>Avatar</Form.Label>
                                    <Form.Control ref={avatar} type="file" placeholder="Ảnh đại diện"
                                        isInvalid={!!fieldErrors['avatar']}
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        {fieldErrors['avatar']}
                                    </Form.Control.Feedback>
                                </Form.Group>

                                <div className="d-grid gap-2 mt-4">
                                    {loading ? <MySpinner /> :
                                        <Button type="submit" variant="primary">
                                            Đăng ký
                                        </Button>
                                    }
                                </div>
                            </Form>

                            {!isGoogleRegister && (
                                <>
                                    <hr className="my-4" />
                                    <div className="d-grid gap-2">
                                        <GoogleLoginButton setMsg={setMsg} />

                                        <div className="text-center">
                                            Đã có tài khoản? <Link to="/login" style={{ textDecoration: 'none' }}>Đăng nhập</Link>
                                        </div>
                                    </div>
                                </>
                            )}
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default Register;
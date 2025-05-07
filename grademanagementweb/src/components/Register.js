import { useRef, useState } from "react";
import { Alert, Button, Form } from "react-bootstrap";
import Apis, { endpoints } from "../configs/Apis";
import MySpinner from "./layouts/MySpinner";
import { useNavigate } from "react-router-dom";

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
    },{
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
        setUser({...user, [field]: value});
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
        <>
            <h1 className="text-center text-success mt-1">ĐĂNG KÝ</h1>

            {msg && <Alert variant="danger">{msg}</Alert>}

            <Form onSubmit={register}>
                {info.map(i => <Form.Control value={user[i.field]} onChange={e => setState(e.target.value, i.field)} className="mt-3 mb-1" key={i.field} type={i.type} placeholder={i.title} required />)}

                <Form.Control ref={avatar} className="mt-3 mb-1" type="file" placeholder="Ảnh đại diện" required />

                {loading === true?<MySpinner />:<Button type="submit" variant="success" className="mt-3 mb-1">Đăng ký</Button>}
            </Form>  
        </>
    )
}

export default Register;
import React from "react";
import { GoogleLogin } from "@react-oauth/google";
import { useNavigate } from "react-router-dom";
import { useContext } from "react";
import { MyDispatcherContext } from "../configs/MyContexts";
import cookie from "react-cookies";
import { jwtDecode } from "jwt-decode";
import Apis, { authApis, endpoints } from "../configs/Apis";

const GoogleLoginButton = ({ setMsg }) => {
    const dispatch = useContext(MyDispatcherContext);
    const navigate = useNavigate();

    const handleLoginGoogle = async (credentialResponse) => {
        const decoded = jwtDecode(credentialResponse.credential);

        try {
            const res = await Apis.post(endpoints["login-google"], {
                token: credentialResponse.credential,
            });

            if (res.data.isNewUser) {
                navigate("/register", { state: { newUser: res.data } });
            } else {
                cookie.save("token", res.data.token);

                let u = await authApis().get(endpoints["profile"]);

                dispatch({
                    type: "login",
                    payload: u.data,
                });

                navigate("/");
            }
        } catch (ex) {
            if (ex.response?.status === 403) {
                const errs = ex.response.data;
                setMsg(errs);
            } else {
                setMsg("Lỗi hệ thống hoặc kết nối.");
            }
        }
    };

    return (
        <GoogleLogin
            onSuccess={handleLoginGoogle}
            onError={() => console.log("Login Failed")}
        />
    );
};

export default GoogleLoginButton
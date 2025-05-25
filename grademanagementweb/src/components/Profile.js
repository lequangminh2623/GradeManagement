import { useEffect, useState } from "react";
import { Card, Container, Row, Col, Spinner } from "react-bootstrap";
import { authApis, endpoints } from "../configs/Apis";

const Profile = () => {
    const [profile, setProfile] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchProfile = async () => {
            try {
                const res = await authApis().get(endpoints["profile"]);
                setProfile(res.data);
            } catch (err) {
                console.error("Lỗi khi lấy thông tin profile:", err);
            } finally {
                setLoading(false);
            }
        };

        fetchProfile();
    }, []);

    if (loading) {
        return (
            <div className="d-flex justify-content-center align-items-center" style={{ height: "100vh" }}>
                <Spinner animation="border" />
            </div>
        );
    }

    if (!profile) {
        return <p className="text-center text-danger">Không thể tải thông tin người dùng.</p>;
    }

    return (
        <Container className="mt-5" style={{ minHeight: "100vh" }}>
            <Row className="justify-content-center">
                <Col md={6}>
                    <Card>
                        <Card.Body className="text-center">
                            <img
                                src={profile.avatar}
                                alt="Avatar"
                                className="rounded-circle mb-3"
                                style={{ width: "150px", height: "150px" }}
                            />
                            <h4>{`${profile.lastName} ${profile.firstName}`}</h4>
                            <p>{profile.email}</p>
                            {profile.role === "ROLE_STUDENT" && <p>{`MSSV: ${profile.student.code}`}</p>}
                            <p>{`Vai trò: ${profile.role}`}</p>
                            <p>{`Ngày tạo: ${profile.createdDate}`}</p>
                            <p>{`Ngày cập nhật: ${profile.updatedDate}`}</p>
                            <p>{`Trạng thái: ${profile.active ? "Hoạt động" : "Không hoạt động"}`}</p>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default Profile;

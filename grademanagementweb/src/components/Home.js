import React, { useContext } from "react";
import { Container, Row, Col, Card } from "react-bootstrap";
import { MyUserContext } from "../configs/MyContexts";
import { useNavigate } from "react-router-dom";

const Home = () => {
    const user = useContext(MyUserContext);
    const nav = useNavigate();

    return (
        <Container className="p-3" style={{ minHeight: "100vh" }}>
            <h1 className="text-center mt-1 mb-5">🎓 Chào mừng đến với <strong>Grade</strong></h1>

            {user.role !== "ROLE_ADMIN" && <Row className="g-4 justify-content-center">
                <Col md={4} xs={10} sm={6}>
                    <Card className="shadow-sm rounded-3 text-center hover-card forum-card h-100"
                        onClick={() => nav('/classrooms')}>
                        <Card.Body>
                            <Card.Title className="fs-4">📚 Lớp học</Card.Title>
                            <Card.Text className="text-muted">Xem danh sách và thông tin lớp học.</Card.Text>
                        </Card.Body>
                    </Card>
                </Col>

                {user.role === "ROLE_STUDENT" ?
                    <Col md={4} xs={10} sm={6}>
                        <Card className="shadow-sm rounded-3 text-center hover-card forum-card h-100"
                            onClick={() => nav('/grades')}>
                            <Card.Body>
                                <Card.Title className="fs-4">💯 Điểm</Card.Title>
                                <Card.Text className="text-muted">Tra cứu điểm học tập chi tiết.</Card.Text>
                            </Card.Body>
                        </Card>
                    </Col> : <Col md={4} xs={10} sm={6}>
                        <Card className="shadow-sm rounded-3 text-center hover-card forum-card h-100"
                            onClick={() => nav('/statistics')}>
                            <Card.Body>
                                <Card.Title className="fs-4">📊 Thống kê</Card.Title>
                                <Card.Text className="text-muted">Thống kê kết quả học tập của sinh viên.</Card.Text>
                            </Card.Body>
                        </Card>
                    </Col>
                }

                <Col md={4} xs={10} sm={6}>
                    <Card className="shadow-sm rounded-3 text-center hover-card forum-card h-100"
                        onClick={() => nav('/chatbox')}>
                        <Card.Body>
                            <Card.Title className="fs-4">🤖 AI chatbot & nhắn tin</Card.Title>
                            <Card.Text className="text-muted">
                                Trò chuyện với trợ lý AI hoặc kết nối với người dùng khác.
                            </Card.Text>
                        </Card.Body>
                    </Card>
                </Col>

            </Row>}

        </Container>
    );
};

export default Home;
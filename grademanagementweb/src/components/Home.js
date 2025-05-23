import React, { useContext } from "react";
import { Container, Row, Col, Card } from "react-bootstrap";
import { MyUserContext } from "../configs/MyContexts";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";

const Home = () => {
    const user = useContext(MyUserContext);
    const nav = useNavigate();
    const { t } = useTranslation();

    return (
        <Container className="p-3" style={{ minHeight: "100vh" }}>
            <h1 className="text-center mt-1 mb-5">🎓 {t('welcome')}!</h1>

            {user.role !== "ROLE_ADMIN" && <Row className="g-4 justify-content-center">
                <Col md={4} xs={10} sm={6}>
                    <Card className="shadow-sm rounded-3 text-center hover-card forum-card h-100"
                        onClick={() => nav('/classrooms')}>
                        <Card.Body>
                            <Card.Title className="fs-4">📚 {t('classrooms')}</Card.Title>
                            <Card.Text className="text-muted">{t('classrooms-title')}</Card.Text>
                        </Card.Body>
                    </Card>
                </Col>

                {user.role === "ROLE_STUDENT" ?
                    <Col md={4} xs={10} sm={6}>
                        <Card className="shadow-sm rounded-3 text-center hover-card forum-card h-100"
                            onClick={() => nav('/grades')}>
                            <Card.Body>
                                <Card.Title className="fs-4">💯 {t('grades')}</Card.Title>
                                <Card.Text className="text-muted">{t('grades-title')}</Card.Text>
                            </Card.Body>
                        </Card>
                    </Col> : <Col md={4} xs={10} sm={6}>
                        <Card className="shadow-sm rounded-3 text-center hover-card forum-card h-100"
                            onClick={() => nav('/statistics')}>
                            <Card.Body>
                                <Card.Title className="fs-4">📊 {t('statistics')}</Card.Title>
                                <Card.Text className="text-muted">{t('statistics-title')}</Card.Text>
                            </Card.Body>
                        </Card>
                    </Col>
                }

                <Col md={4} xs={10} sm={6}>
                    <Card className="shadow-sm rounded-3 text-center hover-card forum-card h-100"
                        onClick={() => nav('/chatbox')}>
                        <Card.Body>
                            <Card.Title className="fs-4">🤖 {t('ai-chat')}</Card.Title>
                            <Card.Text className="text-muted">
                                {t('ai-chat-title')}
                            </Card.Text>
                        </Card.Body>
                    </Card>
                </Col>

            </Row>}

        </Container>
    );
};

export default Home;
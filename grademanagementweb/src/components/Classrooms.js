import { useEffect, useState, useCallback, useContext } from "react";
import { Alert, Button, Card, Col, Container, Row } from "react-bootstrap";
import { authApis, endpoints } from "../configs/Apis";
import { useSearchParams } from "react-router-dom";
import MySpinner from "./layouts/MySpinner";
import { useNavigate } from "react-router-dom";
import { MyUserContext } from "../configs/MyContexts";
import { Pagination } from "react-bootstrap";

const ClassroomList = () => {
    const [classrooms, setClassrooms] = useState([]);
    const [page, setPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const [loading, setLoading] = useState(false);
    const [q] = useSearchParams();
    const navigate = useNavigate();
    const user = useContext(MyUserContext);

    const loadClassrooms = useCallback(async () => {
        try {
            setLoading(true);

            let url = `${endpoints['classrooms']}?page=${page}`;

            const kw = q.get('kw');
            if (kw) {
                url += `&kw=${kw}`;
            }

            const sortBy = q.get('sortBy');
            if (sortBy) {
                url += `&sortBy=${sortBy}`;
            }

            const res = await authApis().get(url);
            const data = res.data;

            setClassrooms(data.content);
            setTotalPages(data.totalPages);
        } catch (ex) {
            console.error("Failed to load classrooms:", ex);
        } finally {
            setLoading(false);
        }
    }, [page, q]);

    useEffect(() => {
        loadClassrooms();
    }, [page, q, loadClassrooms]);

    const handlePageChange = (pageNumber) => {
        setPage(pageNumber);
    };

    return (
        <Container className="p-3" style={{ minHeight: "100vh" }}>
            {classrooms.length === 0 && !loading && (
                <Alert variant="info" className="m-2">
                    Không có lớp học nào!
                </Alert>
            )}

            <Row className="w-100 gy-3">
                {classrooms.map(c => (
                    <Col key={c.id} md={4} xs={6} className="p-2">
                        <Card className="shadow-sm rounded-3 forum-card">
                            <Card.Body>
                                <Card.Title>{c.name}</Card.Title>

                                <Card.Text>
                                    <strong>Trạng thái điểm:</strong> {c.gradeStatus}<br />
                                    <strong>Học phần:</strong> {c.course?.name}<br />
                                    <strong>Học kỳ:</strong> {c.semester?.academicYear.year + ' - ' + c.semester?.semesterType}<br />
                                    <strong>Giảng viên:</strong> {c.lecturer?.lastName} {c.lecturer?.firstName}
                                </Card.Text>

                                <Button
                                    variant="success"
                                    className="me-2"
                                    onClick={() => navigate(`/classrooms/${c.id}/forums`, { state: { classRoomName: c.name } })}>
                                    Diễn đàn
                                </Button>

                                {user.role === "ROLE_LECTURER" && <Button
                                    variant="primary"
                                    className="me-2"
                                    onClick={() => navigate(`/classrooms/${c.id}`)}>
                                    Quản lý điểm
                                </Button>}
                            </Card.Body>
                        </Card>
                    </Col>
                ))}
            </Row>

            {totalPages > 1 && (
                <Pagination className="justify-content-center mt-3">
                    {[...Array(totalPages).keys()].map(number => (
                        <Pagination.Item
                            key={number + 1}
                            active={number + 1 === page}
                            onClick={() => handlePageChange(number + 1)}
                        >
                            {number + 1}
                        </Pagination.Item>
                    ))}
                </Pagination>
            )}

            {loading && <MySpinner />}
        </Container>
    );
};

export default ClassroomList;
import { useEffect, useState } from "react";
import { Alert, Button, Card, Col, Row } from "react-bootstrap";
import { authApis, endpoints } from "../configs/Apis";
import { useSearchParams } from "react-router-dom";
import MySpinner from "./layouts/MySpinner";
import { useNavigate } from "react-router-dom";


const ClassroomList = () => {
    const [classrooms, setClassrooms] = useState([]);
    const [page, setPage] = useState(1);
    const [loading, setLoading] = useState(false);
    const [q] = useSearchParams();
    const navigate = useNavigate();

    const loadClassrooms = async () => {
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

            if (data.length === 0) {
                setPage(0);
            } else {
                if (page === 1) {
                    setClassrooms(data);
                } else {
                    setClassrooms(prev => [...prev, ...data]);
                }
            }
        } catch (ex) {
            console.error("Failed to load classrooms:", ex);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (page > 0) {
            loadClassrooms();
        }
    }, [page, q]);

    useEffect(() => {
        setPage(1);
        setClassrooms([]);
    }, [q]);

    const loadMore = () => {
        if (!loading && page > 0) {
            setPage(prev => prev + 1);
        }
    };

    return (
        <>
            {classrooms.length === 0 && !loading && (
                <Alert variant="info" className="m-2">
                    Không có lớp học nào!
                </Alert>
            )}

            {/* danh sách lớp */}
            <Row className="gy-3">
                {classrooms.map(c => (
                    <Col key={c.id} md={4} xs={6} className="p-2">
                        <Card>
                            <Card.Body>
                                <Card.Title>{c.name}</Card.Title>

                                <Card.Text>
                                    <strong>Trạng thái điểm:</strong> {c.gradeStatus}<br />
                                    <strong>Học phần:</strong> {c.course?.name}<br />
                                    <strong>Học kỳ:</strong> {c.semester?.academicYear.year + ' - ' + c.semester?.semesterType}<br />
                                    <strong>Giảng viên:</strong> {c.lecturer?.lastName} {c.lecturer?.firstName}
                                </Card.Text>

                                <Button
                                    variant="primary"
                                    className="me-2"
                                    onClick={() => navigate(`/classrooms/${c.id}`)}>
                                    Quản lý điểm
                                </Button>

                            </Card.Body>

                        </Card>
                    </Col>
                ))}
            </Row>

            {page > 0 && (
                <div className="text-center m-3">
                    <Button onClick={loadMore} disabled={loading} variant="primary">
                        Xem thêm
                    </Button>
                </div>
            )}

            {loading && <MySpinner />}
        </>
    );
};

export default ClassroomList;

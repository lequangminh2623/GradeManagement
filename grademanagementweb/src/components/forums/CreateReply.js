import React, { useRef, useState } from 'react';
import { Button, Form, Alert, Card } from 'react-bootstrap';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { authApis, endpoints } from '../../configs/Apis';
import MySpinner from '../layouts/MySpinner';

const CreateReply = () => {
    const [reply, setReply] = useState({})
    const image = useRef()
    const [loading, setLoading] = useState(false);
    const [msg, setMsg] = useState("");
    const [fieldErrors, setFieldErrors] = useState({});
    const { classroomId } = useParams();
    const nav = useNavigate();
    const { postId } = useParams()
    const location = useLocation()
    const parentId = location.state?.parentId

    const setState = (value, field) => {
        setReply({ ...reply, [field]: value });
    };

    const validate = () => {
        if (!reply.content || reply.content.trim() === '') {
            setFieldErrors({ content: 'Nội dung không được để trống' })
            return false
        }

        return true
    }

    const handleAddReply = async (e) => {
        e.preventDefault();
        setMsg("");
        setFieldErrors({})

        if (validate() === true) {
            try {
                setLoading(true);

                let form = new FormData();
                for (let key in reply) {
                    form.append(key, reply[key]);
                }

                if (image.current.files[0]) {
                    form.append("file", image.current.files[0]);
                }

                if (parentId) {
                    form.append("parentId", parentId)
                }

                const res = await authApis().post(endpoints['forum-reply'](postId), form, {
                    headers: { "Content-Type": "multipart/form-data" }
                })

                if (parentId) {
                    nav(`/classrooms/${classroomId}/forums/${postId}`, { state: { newChildReply: res.data, parentId: parentId } });
                } else {
                    nav(`/classrooms/${classroomId}/forums/${postId}`, { state: { newReply: res.data } });
                }

            } catch (ex) {
                console.log(ex)
                if (ex.response?.status === 400 && Array.isArray(ex.response.data)) {
                    const errs = {};
                    ex.response.data.forEach(err => {
                        errs[err.field] = err.message;
                    });

                    setFieldErrors(errs);
                } else {
                    setMsg("Lỗi khi phản hồi");
                }
            } finally {
                setLoading(false);
            }
        }
    };

    return (
        <Card className="shadow-sm my-3">
            <Card.Header >
                <h3 className="text-center">Phản hồi</h3>
            </Card.Header>

            <Card.Body className='p-4'>
                {msg && <Alert variant="danger">{msg}</Alert>}

                <Form onSubmit={handleAddReply}>

                    <Form.Group className="mb-3">
                        <Form.Label>Nội dung</Form.Label>
                        <Form.Control
                            as="textarea"
                            rows={5}
                            value={reply['content']}
                            onChange={(e) => setState(e.target.value, "content")}
                            placeholder="Nhập nội dung"
                            isInvalid={!!fieldErrors['content']}
                        />
                        <Form.Control.Feedback type="invalid">
                            {fieldErrors['content']}
                        </Form.Control.Feedback>
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>Hình ảnh (tuỳ chọn)</Form.Label>
                        <Form.Control
                            ref={image}
                            type="file"
                            accept="image/*"
                        />
                    </Form.Group>

                    <div className="d-grid">
                        {loading ? <MySpinner /> :
                            <Button variant="primary" type="submit" disabled={loading}>
                                Gửi phản hồi
                            </Button>
                        }
                    </div>

                </Form>
            </Card.Body>
        </Card>
    );
};

export default CreateReply;

import React, { useRef, useState, useTransition } from 'react';
import { Button, Form, Alert, Card, Col, Image, Row } from 'react-bootstrap';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { authApis, endpoints } from '../../configs/Apis';
import MySpinner from '../layouts/MySpinner';
import { useTranslation } from "react-i18next";
import { capitalizeFirstWord } from "../../utils/utils"

const ForumReplyEdit = () => {
    const location = useLocation()
    const [reply, setReply] = useState(location.state?.reply || {})
    const image = useRef()
    const [loading, setLoading] = useState(false);
    const [msg, setMsg] = useState("");
    const [fieldErrors, setFieldErrors] = useState({});
    const { classroomId, postId } = useParams();
    const nav = useNavigate();
    const [previewImage, setPreviewImage] = useState(reply.image || null)
    const { t } = useTranslation()

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

    const handleUpdateReply = async (e) => {
        e.preventDefault();
        setMsg("");
        setFieldErrors({})

        if (validate() === true) {
            try {
                setLoading(true);

                let form = new FormData();
                for (let key in reply) {
                    if (key !== 'createdDate' && key !== 'updatedDate') {
                        form.append(key, reply[key]);
                    }
                }

                if (image.current.files[0]) {
                    form.append("file", image.current.files[0]);
                }

                const res = await authApis().patch(endpoints['forum-reply-detail'](postId, reply.id), form, {
                    headers: { "Content-Type": "multipart/form-data" }
                })

                alert("Cập nhật phản hồi thành công!")
                nav(-1)
            } catch (ex) {
                console.log(ex)
                if (ex.response?.status === 400 && Array.isArray(ex.response.data)) {
                    const errs = {};
                    ex.response.data.forEach(err => {
                        errs[err.field] = err.message;
                    });

                    setFieldErrors(errs);
                } else {
                    setMsg("Lỗi khi cập nhật phản hồi");
                }
            } finally {
                setLoading(false);
            }
        }
    };

    return (
        <Card className="shadow-sm my-3">
            <Card.Header >
                <h3 className="text-center">{capitalizeFirstWord(`${t('edit')} ${t('reply')}`)}</h3>
            </Card.Header>

            <Card.Body className='p-4'>
                {msg && <Alert variant="danger">{msg}</Alert>}

                <Form onSubmit={handleUpdateReply}>

                    <Form.Group className="mb-3">
                        <Form.Label>{t('content')}</Form.Label>
                        <Form.Control
                            as="textarea"
                            rows={5}
                            value={reply['content']}
                            onChange={(e) => setState(e.target.value, "content")}
                            placeholder={t('enter')}
                            isInvalid={!!fieldErrors['content']}
                        />
                        <Form.Control.Feedback type="invalid">
                            {fieldErrors['content']}
                        </Form.Control.Feedback>
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>{t('image')} ({t('optional')})</Form.Label>
                        <Form.Control
                            ref={image}
                            type="file"
                            accept="image/*"
                            onChange={(e) => {
                                const file = e.target.files[0];
                                if (file) {
                                    const url = URL.createObjectURL(file);
                                    setPreviewImage(url);
                                    console.log(url)
                                }
                            }}
                        />

                        {previewImage && (
                            <div className="text-center m-3">
                                <Image
                                    src={previewImage}
                                    fluid
                                    rounded
                                    className="post-image"
                                />
                            </div>
                        )}
                    </Form.Group>

                    <div className="d-grid">
                        {loading ? <MySpinner /> :
                            <Row className="g-4 align-items-stretch">
                                <Col md={6}>
                                    <Button className='w-100' variant="secondary" disabled={loading}
                                        onClick={() => nav(-1)}>
                                        {t('cancel')}
                                    </Button>
                                </Col>
                                <Col md={6}>
                                    <Button className='w-100' variant="primary" type="submit" disabled={loading}>
                                        {t('save')}
                                    </Button>
                                </Col>
                            </Row>
                        }
                    </div>

                </Form>
            </Card.Body>
        </Card>
    );
};

export default ForumReplyEdit;

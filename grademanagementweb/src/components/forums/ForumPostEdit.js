import React, { useRef, useState } from 'react';
import { Button, Card, Form, Alert, Container, Row, Col, Image } from 'react-bootstrap';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { authApis, endpoints } from '../../configs/Apis';
import MySpinner from '../layouts/MySpinner';
import { useTranslation } from "react-i18next";
import {capitalizeFirstWord} from "../../utils/utils"

const ForumPostEdit = () => {
    const location = useLocation()
    const [post, setPost] = useState(location.state?.post || {})
    const image = useRef()
    const [loading, setLoading] = useState(false)
    const [msg, setMsg] = useState("")
    const [fieldErrors, setFieldErrors] = useState({})
    const [previewImage, setPreviewImage] = useState(post.image || null)
    const nav = useNavigate();
    const { t } = useTranslation()

    const setState = (value, field) => {
        setPost({ ...post, [field]: value });
    }

    const validate = () => {
        if (!post.title || post.title.trim() === '') {
            setFieldErrors({ title: 'Tiêu đề không được để trống' })
            return false
        }

        if (!post.content || post.content.trim() === '') {
            setFieldErrors({ content: 'Nội dung không được để trống' })
            return false
        }

        return true
    }

    const handleUpdatePost = async (e) => {
        e.preventDefault();
        setMsg("");
        setFieldErrors({})

        if (validate() === true) {
            try {
                setLoading(true);

                let form = new FormData();
                for (let key in post) {
                    if (key !== 'createdDate' && key !== 'updatedDate') {
                        form.append(key, post[key]);
                    }
                }

                if (image.current.files[0]) {
                    form.append("file", image.current.files[0]);
                }

                const res = await authApis().patch(endpoints['forum-post-detail'](post.id), form, {
                    headers: { "Content-Type": "multipart/form-data" }
                });

                alert("Cập nhật bài đăng thành công!")
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
                    setMsg("Lỗi khi cập nhật bài đăng");
                }
            } finally {
                setLoading(false);
            }
        }
    }

    if (loading && !post) return <MySpinner />;

    return (
        <Container className="p-3" style={{ minHeight: "100vh" }}>
            <Card className="shadow-sm my-3">
                <Card.Header >
                    <h3 className="text-center">{capitalizeFirstWord(`${t('edit')} ${t('post')}`)}</h3>
                </Card.Header>
                <Card.Body className='p-4'>
                    {msg && <Alert variant="danger">{msg}</Alert>}

                    <Form onSubmit={handleUpdatePost}>
                        <Form.Group className="mb-3">
                            <Form.Label>{t('title')}</Form.Label>
                            <Form.Control
                                type="text"
                                value={post['title'] || ''}
                                onChange={(e) => setState(e.target.value, "title")}
                                placeholder={t('enter')}
                                isInvalid={!!fieldErrors['title']}
                            />
                            <Form.Control.Feedback type="invalid">
                                {fieldErrors['title']}
                            </Form.Control.Feedback>
                        </Form.Group>

                        <Form.Group className="mb-3">
                            <Form.Label>{t('content')}</Form.Label>
                            <Form.Control
                                as="textarea"
                                rows={5}
                                value={post['content'] || ''}
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
        </Container>
    );
};

export default ForumPostEdit;
import React, { useRef, useState } from 'react';
import { Button, Form, Container, Alert, Spinner, Row, Col, Card, Image } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import { authApis, endpoints } from '../../configs/Apis';
import MySpinner from '../layouts/MySpinner';
import { useTranslation } from 'react-i18next';

const CreatePost = () => {
    const [post, setPost] = useState({})
    const image = useRef()
    const [loading, setLoading] = useState(false);
    const [msg, setMsg] = useState("");
    const [fieldErrors, setFieldErrors] = useState({});
    const { classroomId } = useParams();
    const nav = useNavigate();
    const [previewImage, setPreviewImage] = useState(post.image || null)
    const { t } = useTranslation()

    const setState = (value, field) => {
        setPost({ ...post, [field]: value });
    };

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

    const handleAddPost = async (e) => {
        e.preventDefault();
        setMsg("");
        setFieldErrors({})

        if (validate() === true) {
            try {
                setLoading(true);

                let form = new FormData();
                for (let key in post) {
                    form.append(key, post[key]);
                }

                if (image.current.files[0]) {
                    form.append("file", image.current.files[0]);
                }

                const res = await authApis().post(endpoints['forum-posts'](classroomId), form, {
                    headers: { "Content-Type": "multipart/form-data" }
                });

                nav(`/classrooms/${classroomId}/forums`, { state: { newPost: res.data } });
            } catch (ex) {
                console.log(ex)
                if (ex.response?.status === 400 && Array.isArray(ex.response.data)) {
                    const errs = {};
                    ex.response.data.forEach(err => {
                        errs[err.field] = err.message;
                    });

                    setFieldErrors(errs);
                } else {
                    setMsg("Lỗi khi tạo bài đăng");
                }
            } finally {
                setLoading(false);
            }
        }
    };

    return (
        <Card className="shadow-sm my-3">
            <Card.Header >
                <h3 className="text-center">{t('new-post')}</h3>
            </Card.Header>

            <Card.Body className='p-4'>
                {msg && <Alert variant="danger">{msg}</Alert>}

                <Form onSubmit={handleAddPost}>

                    <Form.Group className="mb-3">
                        <Form.Label>{t('title')}</Form.Label>
                        <Form.Control
                            type="text"
                            value={post['title']}
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
                            value={post['content']}
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
                            <Button variant="primary" type="submit" disabled={loading}>
                                {t('do-post')}
                            </Button>
                        }
                    </div>

                </Form>
            </Card.Body>
        </Card>
    );
};

export default CreatePost;

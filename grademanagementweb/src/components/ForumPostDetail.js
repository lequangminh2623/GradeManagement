import React, { useContext, useEffect, useState } from 'react';
import { Button, Card, Col, Image, OverlayTrigger, Tooltip, Row, Container, Alert } from 'react-bootstrap';
import { PiNotePencil } from "react-icons/pi";
import { RiDeleteBin7Line } from "react-icons/ri";
import { useParams, useNavigate, Link, useLocation } from 'react-router-dom';
import { MyUserContext } from '../configs/MyContexts';
import MySpinner from './layouts/MySpinner';
import { authApis, endpoints } from '../configs/Apis';
import ForumReply from './ForumReply';
import { checkPermission, checkCanEdit, formatVietnamTime } from '../utils/utils';

const ForumPostDetail = () => {
    const { postId } = useParams();
    const nav = useNavigate();
    const [post, setPost] = useState(null);
    const [perm, setPerm] = useState(false);
    const [canEditOrDelete, setCanEditOrDelete] = useState(false);
    const user = useContext(MyUserContext);
    const [loading, setLoading] = useState(false)
    const location = useLocation()
    const { classRoomName } = location.state || ''


    const loadForumPost = async () => {
        try {
            setLoading(true)

            const res = await authApis().get(endpoints['forum-post-detail'](postId))
            setPost(res.data)

        } catch (ex) {
            console.error(ex)
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        loadForumPost()
    }, [postId])

    useEffect(() => {
        if (post) {
            if (checkPermission(post.user.id, user.id) === true) {
                setPerm(true)
                setCanEditOrDelete(checkCanEdit(post.createdDate))
            }
        }
    }, [post])

    if (loading && !post) return <MySpinner />

    return (
        <Container className="p-3" style={{ minHeight: "100vh" }}>
            {loading && <MySpinner />}
            <h3 className='mb-3'>{classRoomName}</h3>
            {post && <>
                <Card className="shadow-sm">
                    <Card.Header className="d-flex align-items-center bg-light">
                        <Image
                            src={post.user.avatar}
                            roundedCircle
                            width={50}
                            height={50}
                            className="me-3"
                        />
                        <div>
                            <div className="fw-bold">{post.user.firstName} {post.user.lastName}</div>
                            <div className="text-muted" style={{ fontSize: '0.85rem' }}>
                                {formatVietnamTime(post.createdDate)}
                            </div>
                        </div>
                    </Card.Header>
                    <Card.Body>
                        <div className='d-flex align-items-center justify-content-between'>
                            <Card.Title>{post.title}</Card.Title>

                            {perm && <div>
                                <OverlayTrigger
                                    placement="top"
                                    overlay={
                                        <Tooltip>Bạn chỉ có thể chỉnh sửa trong 30 phút</Tooltip>
                                    }
                                >
                                    <span className="me-2">
                                        <Button
                                            className="rounded-5"
                                            variant="warning"
                                            disabled={!canEditOrDelete}
                                        >
                                            <PiNotePencil size={25} />
                                        </Button>
                                    </span>
                                </OverlayTrigger>

                                <OverlayTrigger
                                    placement="top"
                                    overlay={
                                        <Tooltip>Bạn chỉ có thể xoá trong 30 phút</Tooltip>
                                    }
                                >
                                    <span className="me-2">
                                        <Button
                                            className='rounded-5'
                                            variant="danger"
                                            disabled={!canEditOrDelete}
                                        >
                                            <RiDeleteBin7Line size={25} />
                                        </Button>
                                    </span>
                                </OverlayTrigger>
                            </div>
                            }
                        </div>
                        {post.image && (
                            <div className="text-center">
                                <Link to={post.image} target='_blank'>
                                    <Image
                                        src={post.image}
                                        fluid
                                        rounded
                                        className="post-image"
                                    />
                                </Link>
                            </div>
                        )}
                        <Card.Text className='px-5'>{post.content}</Card.Text>

                    </Card.Body>
                </Card>

                <Row className="mt-4">
                    <Col>
                        <h5>Phản hồi</h5>
                        {post.forumReplies.length > 0 ? post.forumReplies.map(i =>
                            <div className='mb-3'>
                                <ForumReply reply={i} />
                            </div>
                        ) : <div className='text-center'>
                            Không có phản hồi!
                        </div>}
                    </Col>
                </Row>
            </>}
        </Container>
    );
};

export default ForumPostDetail;

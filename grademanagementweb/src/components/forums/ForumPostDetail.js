import React, { useContext, useEffect, useState } from 'react';
import { Button, Card, Col, Image, OverlayTrigger, Tooltip, Row, Container } from 'react-bootstrap';
import { useParams, useNavigate, Link, useLocation, Outlet } from 'react-router-dom';
import { MyUserContext } from '../../configs/MyContexts';
import MySpinner from '../layouts/MySpinner';
import { authApis, endpoints } from '../../configs/Apis';
import ForumReply from './ForumReply';
import { checkPermission, checkCanEdit, formatVietnamTime } from '../../utils/utils';
import { FaPenToSquare, FaTrashCan } from "react-icons/fa6";

const ForumPostDetail = () => {
    const { classroomId, postId } = useParams();
    const nav = useNavigate();
    const [post, setPost] = useState(null);
    const [replies, setReplies] = useState([])
    const [perm, setPerm] = useState(false);
    const [canEditOrDelete, setCanEditOrDelete] = useState(false);
    const user = useContext(MyUserContext);
    const [loading, setLoading] = useState(false)
    const location = useLocation()
    const [{ classRoomName }, setClassRoomName] = useState(location.state || '')
    const isAddPage = location.pathname.endsWith(`forums/${postId}/add`);
    const [page, setPage] = useState(1)


    const loadForumPost = async () => {
        try {
            setLoading(true)

            const url = `${endpoints['forum-post-detail'](postId)}?page=${page}`
            const res = await authApis().get(url)
            setPost(res.data.content)
            setReplies(prev => [...prev, ...res.data.content.forumReplies]);

            if (page >= res.data.totalPages) {
                setPage(0);
            }

        } catch (ex) {
            console.error(ex)
        } finally {
            setLoading(false)
        }
    }

    const handleDeletePost = async () => {
        if (!window.confirm("Bạn có chắc chắn muốn xoá bài đăng này?")) return;

        try {
            await authApis().delete(endpoints['forum-post-detail'](post.id));

            alert('Xóa bài đăng thành công!')
            nav(`/classrooms/${classroomId}/forums`)
        } catch (ex) {
            console.error("Delete error:", ex);
            alert("Xoá bài đăng thất bại!");
        }
    }

    const toggleAddReply = () => {
        if (isAddPage) {
            nav(`/classrooms/${classroomId}/forums/${postId}`);
        } else {
            nav(`/classrooms/${classroomId}/forums/${postId}/add`);
        }
    }

    const handleReplyDeleted = (deletedId) => {
        setReplies(prev => prev.filter(r => r.id !== deletedId));
        alert('Xóa phản hồi thành công!')
    }

    useEffect(() => {
        if (post) {
            if (checkPermission(post.user.id, user.id) === true) {
                setPerm(true)
                setCanEditOrDelete(checkCanEdit(post.createdDate))
            }
        }
    }, [post])

    const loadMore = () => {
        if (!loading && page > 0)
            setPage(page + 1);
    }

    useEffect(() => {
        if (page !== 1)
            setPage(1);
        setPost()
        setReplies([])
    }, []);

    useEffect(() => {
        if (page > 0)
            loadForumPost();
    }, [page]);

    useEffect(() => {
        if (location.state?.newReply) {
            alert('Phản hồi thành công!')
            setReplies(prev => [location.state.newReply, ...prev]);
            nav(location.pathname, { replace: true, state: { ...location.state, newReply: null } });
        }
    }, [location.state?.newReply]);

    if (loading && !post) return <MySpinner />

    return (
        <Container className="p-3" style={{ minHeight: "100vh" }}>
            <h3 className='mb-3'>Chi tiết bài đăng</h3>
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
                                            size='sm'
                                            className="rounded-5"
                                            variant="outline"
                                            disabled={!canEditOrDelete}
                                            onClick={() => nav(`/classrooms/${classroomId}/forums/${post.id}/edit`, { state: { post: post } })}
                                        >
                                            <FaPenToSquare size={22} />
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
                                            size='sm'
                                            className='rounded-5'
                                            variant="outline"
                                            onClick={handleDeletePost}
                                            disabled={!canEditOrDelete}
                                        >
                                            <FaTrashCan size={22} />
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
                        <div className="d-flex justify-content-between mb-3">
                            <h5>Phản hồi</h5>
                            <Button variant="success" onClick={toggleAddReply}>
                                {isAddPage ? 'Đóng' : '+ Thêm phản hồi'}
                            </Button>
                        </div>

                        {isAddPage && <Outlet />}

                        {replies.length > 0 ? replies.map(i =>
                            <div className='mb-3' key={i.id}>
                                <ForumReply reply={i} onReplyDeleted={handleReplyDeleted} />
                            </div>
                        ) : <div className='text-center'>
                            Không có phản hồi!
                        </div>}

                        {loading && <MySpinner />}

                        {page > 0 && <div className="text-center mb-2 mt-3">
                            <Button variant="primary" onClick={loadMore}>Xem thêm...</Button>
                        </div>}
                    </Col>
                </Row>
            </>}
        </Container>
    );
};

export default ForumPostDetail;

import React, { useEffect, useState } from 'react';
import { authApis, endpoints } from '../configs/Apis';
import { Alert, Button, Col, Container, Row } from 'react-bootstrap';
import MySpinner from './layouts/MySpinner';
import { Outlet, useLocation, useNavigate, useParams, useSearchParams } from 'react-router-dom';
import ForumPost from './ForumPost';

const Forum = () => {
    const [posts, setPosts] = useState([])
    const [loading, setLoading] = useState(false);
    const [page, setPage] = useState(1);
    const [q] = useSearchParams();
    const { classroomId } = useParams();
    const nav = useNavigate()
    const location = useLocation();
    const [{ classRoomName }, setClassRoomName] = useState(location.state || '')

    const isAddPage = location.pathname.endsWith('/add');

    const toggleAddPost = () => {
        if (isAddPage) {
            nav(`/classrooms/${classroomId}/forums`);
        } else {
            nav(`/classrooms/${classroomId}/forums/add`);
        }
    };

    const loadPosts = async () => {
        try {
            setLoading(true)

            let url = `${endpoints['forum-posts'](classroomId)}?page=${page}`;

            const kw = q.get('kw');
            if (kw) {
                url = `${url}&kw=${kw}`;
            }

            const res = await authApis().get(url);
            setPosts(prev => [...prev, ...res.data.content]);

            if (page >= res.data.totalPages) {
                setPage(0);
            }

        } catch (ex) {
            console.error(ex)
        } finally {
            setLoading(false)
        }
    }

    const loadMore = () => {
        if (!loading && page > 0)
            setPage(page + 1);
    }

    const handlePostDeleted = (deletedId) => {
        setPosts(prev => prev.filter(p => p.id !== deletedId));
    }

    useEffect(() => {
        if (page !== 1)
            setPage(1);
        setPosts([])
    }, [q]);

    useEffect(() => {
        if (page > 0)
            loadPosts();
    }, [q, page]);

    useEffect(() => {
        if (location.state?.newPost) {
            alert('Đăng bài thành công!')
            setPosts(prev => [location.state.newPost, ...prev]);
            nav(location.pathname, { replace: true, state: { ...location.state, newPost: null } });
        }
    }, [location.state?.newPost]);

    if (loading && posts.length === 0) return <MySpinner />;

    return (
        <Container className="p-3" style={{ minHeight: "100vh" }}>
            <div className="d-flex justify-content-between mb-3">
                <h3>{classRoomName}</h3>
                <Button variant="success" onClick={toggleAddPost}>
                    {isAddPage ? 'Đóng' : '+ Bài đăng mới'}
                </Button>
            </div>

            <Outlet />

            {posts.length > 0 ? (
                <Row className="g-4 align-items-stretch">
                    {posts.map(post => (
                        <Col md={6} key={post.id} >
                            <ForumPost post={post} classRoomName={classRoomName} onPostDeleted={handlePostDeleted} />
                        </Col>
                    ))}
                </Row>
            ) : <Alert variant="info" className="m-2">
                Không có bài đăng!
            </Alert>
            }

            {loading && <MySpinner />}

            {page > 0 && <div className="text-center mb-2 mt-3">
                <Button variant="primary" onClick={loadMore}>Xem thêm...</Button>
            </div>}
        </Container >
    );
};

export default Forum;

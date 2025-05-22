import { useContext, useEffect, useState } from "react";
import { Button, Card, Image, OverlayTrigger, Tooltip } from "react-bootstrap";
import { Link, Outlet, useLocation, useNavigate, useParams } from "react-router-dom";
import { MyUserContext } from "../../configs/MyContexts";
import { checkPermission, checkCanEdit, formatVietnamTime } from '../../utils/utils';
import { FaPenToSquare, FaTrashCan } from "react-icons/fa6";
import { authApis, endpoints} from "../../configs/Apis"
import MySpinner from "../layouts/MySpinner";
import { FaReply } from "react-icons/fa6";
import { IoCloseSharp } from "react-icons/io5";


const ForumReply = ({ reply, onReplyDeleted }) => {
    const user = useContext(MyUserContext);
    const [perm, setPerm] = useState(false);
    const [canEditOrDelete, setCanEditOrDelete] = useState(false);
    const { postId } = useParams();
    const [showChildren, setShowChildren] = useState(false);
    const [childReplies, setChildReplies] = useState([]);
    const [loadingChildren, setLoadingChildren] = useState(false);
    const [loadedOnce, setLoadedOnce] = useState(false);
    const location = useLocation()
    const isAddPage = location.pathname.endsWith(`/replies/${reply.id}/add`);
    const { classroomId } = useParams()
    const [page, setPage] = useState(1)
    const nav = useNavigate()

    const handleDeleteReply = async () => {
        if (!window.confirm("Bạn có chắc chắn muốn xoá phản hồi này?")) return;

        try {
            await authApis().delete(endpoints['forum-reply-detail'](postId, reply.id));
            onReplyDeleted(reply.id);
        } catch (ex) {
            console.error("Delete error:", ex);
            alert("Xoá phản hồi thất bại!");
        }
    }

    const loadChildReplies = async () => {
        setLoadingChildren(true);
        try {
            const url = `${endpoints['forum-child-replies'](postId, reply.id)}?page=${page}`;
            const res = await authApis().get(url);

            setChildReplies((prev) => [...prev, ...res.data.content]);

            if (page >= res.data.totalPages) {
                setPage(0);
            }

            setLoadedOnce(true);
        } catch (err) {
            console.error("Failed to load child replies:", err);
        } finally {
            setLoadingChildren(false);
        }
    };

    const toggleChildren = async () => {
        setShowChildren(prev => !prev);
        if (!loadedOnce) {
            await loadChildReplies();
        }
    }

    const loadMore = () => {
        if (!loadingChildren && page > 0)
            setPage(page + 1);
    }

    const toggleAddReply = () => {
        if (isAddPage) {
            nav(`/classrooms/${classroomId}/forums/${postId}`);
        } else {
            nav(`/classrooms/${classroomId}/forums/${postId}/replies/${reply.id}/add`, { state: { parentId: reply.id } });
        }
    }

    const handleChildReplyDeleted = (deletedId) => {
        setChildReplies((prev) => prev.filter((child) => child.id !== deletedId));
    }

    useEffect(() => {
        if (page !== 1)
            setPage(1);
        setChildReplies([])
    }, []);

    useEffect(() => {
        if (page > 0)
            loadChildReplies();
    }, [page]);

    useEffect(() => {
        if (checkPermission(reply.user.id, user.id)) {
            setPerm(true);
            setCanEditOrDelete(checkCanEdit(reply.createdDate));
        }
    }, []);

    useEffect(() => {
        if (location.state?.newChildReply) {
            if (location.state?.parentId === reply.id) {
                alert("Phản hồi thành công!");
                setChildReplies(prev => [location.state.newChildReply, ...prev]);
                setShowChildren(true);
                nav(location.pathname, { replace: true, state: { ...location.state, newChildReply: null } });
            }
        }
    }, [location.state?.newChildReply]);


    return (
        <Card className="shadow-sm m-1">
            <Card.Header className="d-flex align-items-center bg-light">
                <Image src={reply.user.avatar} roundedCircle width={50} height={50} className="me-3" />
                <div>
                    <div className="fw-bold">{reply.user.firstName} {reply.user.lastName}</div>
                    <div className="text-muted" style={{ fontSize: '0.85rem' }}>
                        {formatVietnamTime(reply.createdDate)}
                    </div>
                </div>
            </Card.Header>
            <Card.Body>
                <div className="d-flex justify-content-end mb-2">
                    {perm && (
                        <>
                            <OverlayTrigger placement="top" overlay={<Tooltip>Bạn chỉ có thể chỉnh sửa trong 30 phút</Tooltip>}>
                                <span className="me-2">
                                    <Button size="sm" className="rounded-5" variant="outline" disabled={!canEditOrDelete}
                                        onClick={() => nav(`/classrooms/${classroomId}/forums/${postId}/replies/${reply.id}/edit`, { state: { reply: reply } })}
                                    >
                                        <FaPenToSquare size={22} />
                                    </Button>
                                </span>
                            </OverlayTrigger>
                            <OverlayTrigger placement="top" overlay={<Tooltip>Bạn chỉ có thể xoá trong 30 phút</Tooltip>}>
                                <span className="me-2">
                                    <Button size="sm" className="rounded-5" variant="outline" onClick={handleDeleteReply} disabled={!canEditOrDelete}>
                                        <FaTrashCan size={22} />
                                    </Button>
                                </span>
                            </OverlayTrigger>
                        </>
                    )}
                </div>

                {reply.image && (
                    <div className="text-center">
                        <Link to={reply.image} target="_blank">
                            <Image src={reply.image} fluid rounded className="post-image" />
                        </Link>
                    </div>
                )}

                <Card.Text className="px-5">{reply.content}</Card.Text>

                <Button
                    variant="outline-primary"
                    size="sm"
                    className="mt-3 me-2"
                    onClick={toggleChildren}
                >
                    {showChildren ? 'Ẩn phản hồi' : 'Hiển thị phản hồi'}
                </Button>

                <Button
                    variant="ouline"
                    size="sm"
                    className="mt-3 rounded-5"
                    onClick={toggleAddReply}
                >
                    {isAddPage ? <IoCloseSharp size={23} /> : <FaReply size={22} />}
                </Button>

                {isAddPage && <div className="p-2"><Outlet /></div>}

                {loadingChildren && <MySpinner />}

                {showChildren && (
                    <div className="mt-3 ms-4">
                        {childReplies.map(child => (
                            <ForumReply
                                key={child.id}
                                reply={child}
                                onReplyDeleted={handleChildReplyDeleted}
                            />
                        ))}

                        {page > 0 && <div className="text-center mb-2 mt-3">
                            <Button variant="primary" onClick={loadMore}> Xem thêm phản hồi...</Button>
                        </div>}
                    </div>
                )}

            </Card.Body>
        </Card>
    );
}

export default ForumReply
import { useContext, useEffect, useState } from "react";
import { Button, Card, Image, OverlayTrigger, Tooltip } from "react-bootstrap";
import { Link, useParams } from "react-router-dom";
import { MyUserContext } from "../configs/MyContexts";
import { checkPermission, checkCanEdit, formatVietnamTime } from '../utils/utils';
import { FaPenToSquare, FaTrashCan } from "react-icons/fa6";
import { authApis, endpoints } from '../configs/Apis';
import { FaPlus } from "react-icons/fa6";
import MySpinner from "./layouts/MySpinner";


const ForumReply = ({ reply, onReplyDeleted }) => {
    const user = useContext(MyUserContext);
    const [perm, setPerm] = useState(false);
    const [canEditOrDelete, setCanEditOrDelete] = useState(false);
    const { postId } = useParams();
    const [showChildren, setShowChildren] = useState(false);
    const [childReplies, setChildReplies] = useState([]);
    const [loadingChildren, setLoadingChildren] = useState(false);
    const [loadedOnce, setLoadedOnce] = useState(false);

    const handleDeleteReply = async () => {
        if (!window.confirm("Bạn có chắc chắn muốn xoá phản hồi này?")) return;

        try {
            await authApis().delete(endpoints['forum-reply-detail'](postId, reply.id));
            onReplyDeleted(reply.id);
        } catch (ex) {
            console.error("Delete error:", ex);
            alert("Xoá phản hồi thất bại!");
        }
    };

    const toggleChildren = async () => {
        setShowChildren(prev => !prev);

        if (!loadedOnce) {
            setLoadingChildren(true);
            try {
                const res = await authApis().get(endpoints['forum-child-replies'](postId, reply.id));
                console.log(res.data)

                setChildReplies(res.data);
                setLoadedOnce(true);
            } catch (err) {
                console.error("Failed to load child replies:", err);
            } finally {
                setLoadingChildren(false);
            }
        }
    };

    useEffect(() => {
        if (checkPermission(reply.user.id, user.id)) {
            setPerm(true);
            setCanEditOrDelete(checkCanEdit(reply.createdDate));
        }
    }, []);

    return (
        <Card className="shadow-sm">
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
                    <Button size="sm" className="rounded-5" variant="outline">
                        <FaPlus size={22} />
                    </Button>
                    {perm && (
                        <>
                            <OverlayTrigger placement="top" overlay={<Tooltip>Chỉnh sửa trong 30 phút</Tooltip>}>
                                <span className="me-2">
                                    <Button size="sm" className="rounded-5" variant="outline" disabled={!canEditOrDelete}>
                                        <FaPenToSquare size={22} />
                                    </Button>
                                </span>
                            </OverlayTrigger>
                            <OverlayTrigger placement="top" overlay={<Tooltip>Xoá trong 30 phút</Tooltip>}>
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
                    className="mt-3"
                    onClick={toggleChildren}
                >
                    {showChildren ? 'Ẩn phản hồi' : 'Hiển thị phản hồi'}
                </Button>

                {loadingChildren && <MySpinner />}

                {showChildren && (
                    <div className="mt-3 ms-4">
                        {childReplies.map(child => (
                            <ForumReply
                                key={child.id}
                                reply={child}
                                onReplyDeleted={onReplyDeleted}
                            />
                        ))}
                    </div>
                )}
            </Card.Body>
        </Card>
    );
}

export default ForumReply
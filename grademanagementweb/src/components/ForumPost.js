import "../styles/ForumPost.css";
import React, { useContext, useEffect, useState } from 'react';
import { Button, Card, Image, OverlayTrigger, Tooltip } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import { MyUserContext } from '../configs/MyContexts';
import { checkPermission, checkCanEdit, formatVietnamTime } from '../utils/utils';
import { FaPenToSquare, FaEye, FaTrashCan } from "react-icons/fa6";
import { authApis, endpoints } from "../configs/Apis";

const ForumPost = ({ post, classRoomName, onPostDeleted }) => {
    const nav = useNavigate();
    const [perm, setPerm] = useState(false)
    const [canEditOrDelete, setCanEditOrDelete] = useState(false)
    const user = useContext(MyUserContext)
    const { classroomId } = useParams();

    const handleDeletePost = async () => {
        if (!window.confirm("Bạn có chắc chắn muốn xoá bài đăng này?")) return;

        try {
            await authApis().delete(endpoints['forum-post-detail'](post.id));
            onPostDeleted(post.id);
        } catch (ex) {
            console.error("Delete error:", ex);
            alert("Xoá bài viết thất bại!");
        }
    }

    useEffect(() => {
        if (checkPermission(post.user.id, user.id) === true) {
            setPerm(true)
            setCanEditOrDelete(checkCanEdit(post.createdDate))
        }
    }, [])

    return (
        <Card className="shadow-sm forum-card">
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
                <Card.Title>{post.title}</Card.Title>

                {post.image ? (
                    <div className="post-image-container text-center">
                        <Image
                            src={post.image}
                            fluid
                            rounded
                            className="post-image"
                        />
                    </div>
                ) : <div className="post-image-container">
                </div>}

                <div className='d-flex justify-content-end post-image-container'>
                    <Button
                        size="sm"
                        className="rounded-5 me-2"
                        variant="outline"
                        onClick={() => nav(`/classrooms/${classroomId}/forums/${post.id}`, { state: { classRoomName: classRoomName } })}
                    >
                        <FaEye size={25} />
                    </Button>

                    {perm && <>
                        <OverlayTrigger
                            placement="top"
                            overlay={
                                <Tooltip>Bạn chỉ có thể chỉnh sửa trong 30 phút</Tooltip>
                            }
                        >
                            <span className="me-2">
                                <Button
                                    size="sm"
                                    className="rounded-5"
                                    variant="outline"
                                    disabled={!canEditOrDelete}
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
                                    size="sm"
                                    className='rounded-5'
                                    variant="outline"
                                    disabled={!canEditOrDelete}
                                    onClick={handleDeletePost}
                                >
                                    <FaTrashCan size={22} />
                                </Button>
                            </span>
                        </OverlayTrigger>
                    </>
                    }
                </div>

            </Card.Body>
        </Card>
    );
};

export default ForumPost;

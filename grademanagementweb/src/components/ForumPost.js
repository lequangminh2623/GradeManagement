import "../styles/ForumPost.css";
import React, { useContext, useEffect, useState } from 'react';
import { Button, Card, Col, Image, OverlayTrigger, Tooltip } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { PiNotePencil } from "react-icons/pi";
import { RiDeleteBin7Line } from "react-icons/ri";
import { MyUserContext } from '../configs/MyContexts';
import { checkPermission, checkCanEdit, formatVietnamTime } from '../utils/utils';
import { FaRegEye } from "react-icons/fa";


const ForumPost = ({ post, classRoomName }) => {
    const nav = useNavigate();
    const [perm, setPerm] = useState(false)
    const [canEditOrDelete, setCanEditOrDelete] = useState(false)
    const user = useContext(MyUserContext)

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
                        className="rounded-5 me-2"
                        variant="info"
                        onClick={() => nav(`/forum/${post.id}`, { state: { classRoomName: classRoomName } })}
                    >
                        <FaRegEye size={25} />
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
                    </>
                    }
                </div>

            </Card.Body>
        </Card>
    );
};

export default ForumPost;

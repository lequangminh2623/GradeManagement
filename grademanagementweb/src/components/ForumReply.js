import { useContext, useState } from "react";
import { Card, Image } from "react-bootstrap";
import { Link } from "react-router-dom";
import { MyUserContext } from "../configs/MyContexts";
import { checkPermission, checkCanEdit, formatVietnamTime } from '../utils/utils';

const ForumReply = ({ reply }) => {

    return (
        <Card className="shadow-sm">
            <Card.Header className="d-flex align-items-center bg-light">
                <Image
                    src={reply.user.avatar}
                    roundedCircle
                    width={50}
                    height={50}
                    className="me-3"
                />
                <div>
                    <div className="fw-bold">{reply.user.firstName} {reply.user.lastName}</div>
                    <div className="text-muted" style={{ fontSize: '0.85rem' }}>
                        {formatVietnamTime(reply.createdDate)}
                    </div>
                </div>
            </Card.Header>
            <Card.Body>
                {reply.image && (
                    <div className="text-center">
                        <Link to={reply.image} target='_blank'>
                            <Image
                                src={reply.image}
                                fluid
                                rounded
                                className="post-image"
                            />
                        </Link>
                    </div>
                )}
                <Card.Text className='px-5'>{reply.content}</Card.Text>
            </Card.Body>
        </Card>
    )
}

export default ForumReply;
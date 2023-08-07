import React, { useState } from 'react';
import styled from 'styled-components';
import { Main, MarginFrame } from '../../style';
import { MyPageTab } from '../../components/TabBar/MypageTab';
import ProfileImage from '../../components/My/ProfileImage';
import OptionMenu from '../../components/My/OptionMenu';
import LikesUnlikes from '../../components/Box/LikesUnlikes';
import ProfileStats from '../../components/My/ProfileStats';
import ProfileTabs from '../../components/My/ProfileTabs';
import BottomNav from '../../components/common/BottomNav';
import axios from '../../api/apiController';

// 중복 데이터를 변수로 추출
const perfumeImages = [
  'src/assets/img/perfume1.png',
  'src/assets/img/perfume_aqua.png',
  'src/assets/img/perfume_doson.png',
  'src/assets/img/perfume1.png',
  'src/assets/img/perfume_doson.png',
];

interface TextProp {
  size?: string;
  fontWeight?: string;
  color?: string;
  textalign?: string;
}

const MypageText = styled.div<TextProp>`
  font-size: ${(props) => props.size || 'inherit'};
  font-weight: ${(props) => props.fontWeight || 'normal'};
  color: ${(props) => props.color || 'inherit'};
  text-align: ${(props) => props.textalign || 'left'};
`;

// feeds 배열을 두 개의 열로 나누는 함수
const splitFeeds = (arr: string[]): [string[], string[]] => {
  const oddColumn: string[] = [];
  const evenColumn: string[] = [];

  arr.forEach((feed, idx) => {
    if (idx % 2 === 1) {
      evenColumn.push(feed);
    } else {
      oddColumn.push(feed);
    }
  });

  return [oddColumn, evenColumn];
};

// 컨테이너 스타일 정의
const MypageContainer = styled.div`
  display: flex;
  padding: 10px 50px;
  justify-content: center;
`;

// 열 스타일 정의
const Column = styled.div`
  flex: 1;
`;

export default function Mypage() {
  const [nowActive, setNowActive] = useState('following');
  const [firstColumnFeeds, secondColumnFeeds] = splitFeeds(perfumeImages);

  return (
    <Main>
      <MarginFrame margin="10px 0">
        <OptionMenu />
        <ProfileImage />
        <MypageText size="18px" fontWeight="bold" textalign="center">
          userDto.nickname
        </MypageText>
        <LikesUnlikes />
        <ProfileStats />
        <MyPageTab setNowActive={setNowActive} />
        <MypageContainer>
          <Column>
            {firstColumnFeeds.map((img, idx) => (
              <ProfileTabs key={idx} feed={img} />
            ))}
          </Column>
          <Column>
            {secondColumnFeeds.map((img, idx) => (
              <ProfileTabs key={idx} feed={img} />
            ))}
          </Column>
        </MypageContainer>
      </MarginFrame>

      <BottomNav />
    </Main>
  );
}
